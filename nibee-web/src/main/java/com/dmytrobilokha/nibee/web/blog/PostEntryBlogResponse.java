package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.post.PostService;
import com.dmytrobilokha.nibee.web.NavigablePage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.util.Optional;

class PostEntryBlogResponse extends BlogResponse {

    private static final String POST_ENTRY_FILE = "_post_.html";

    private final ConfigService configService;
    private final PostService postService;
    private final FileService fileService;
    final String postName;

    PostEntryBlogResponse(ConfigService configService, PostService postService, FileService fileService, String postName) {
        this.configService = configService;
        this.postService = postService;
        this.fileService = fileService;
        this.postName = postName;
    }

    @Override
    void respond(HttpServletRequest req, HttpServletResponse resp) {
        Optional<Post> postOptional = postService.findPostByName(postName);
        if (!postOptional.isPresent()) {
            respondWithError(resp, HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Post post = postOptional.get();
        servePost(req, resp, post);
    }

    private void servePost(HttpServletRequest req, HttpServletResponse resp, Post post) {
        Path postEntryPath = getPostFilePath(configService, post, POST_ENTRY_FILE);
        if (!fileService.isFileRegularAndReadable(postEntryPath)) {
            respondWithError(resp, HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String contentBase = req.getRequestURI().endsWith("/") ? req.getRequestURI() : (req.getRequestURI() + '/');
        PostModel postModel = new PostModel("file://localhost" + postEntryPath.toString()
                , contentBase, post.getTags());
        postModel.putInRequest(req);
        NavigablePage.POST.forwardTo(req, resp);
    }

}