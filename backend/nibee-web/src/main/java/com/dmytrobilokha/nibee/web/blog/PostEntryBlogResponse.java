package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.data.PostWithTags;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.post.PostService;
import com.dmytrobilokha.nibee.web.NavigablePage;
import com.dmytrobilokha.nibee.web.comment.CommentsModelCreator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;

class PostEntryBlogResponse extends BlogResponse {

    private static final String POST_ENTRY_FILE = "_post_.html";

    final String postName;
    private final ConfigService configService;
    private final PostService postService;
    private final FileService fileService;
    private final CommentsModelCreator commentsModelCreator;

    PostEntryBlogResponse(ConfigService configService, PostService postService, FileService fileService
            , CommentsModelCreator commentsModelCreator, String postName) {
        this.configService = configService;
        this.postService = postService;
        this.fileService = fileService;
        this.commentsModelCreator = commentsModelCreator;
        this.postName = postName;
    }

    @Override
    void respond(HttpServletRequest req, HttpServletResponse resp) {
        PostWithTags post = postService.findPostByName(postName);
        if (post == null) {
            respondWithError(resp, HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        servePost(req, resp, post);
    }

    private void servePost(HttpServletRequest req, HttpServletResponse resp, PostWithTags post) {
        Path postEntryPath = getPostFilePath(configService, post.getPath(), POST_ENTRY_FILE);
        if (!fileService.isFileRegularAndReadable(postEntryPath)) {
            respondWithError(resp, HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String contentBase = req.getRequestURI().endsWith("/") ? req.getRequestURI() : req.getRequestURI() + '/';
        PostModel postModel = new PostModel("file://localhost" + postEntryPath.toString(), contentBase, post);
        postModel.putInRequest(req);
        commentsModelCreator.createAndPutInRequest(post.getId(), post.isCommentAllowed(), req);
        NavigablePage.POST.forwardTo(req, resp);
    }

}
