package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.post.PostService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

class PostResourceBlogResponse extends BlogResponse {

    private final ConfigService configService;
    private final PostService postService;
    private final FileService fileService;
    private final String postName;
    private final String postResource;

    PostResourceBlogResponse(ConfigService configService, PostService postService, FileService fileService
            , String postName, String postResource) {
        this.configService = configService;
        this.postService = postService;
        this.fileService = fileService;
        this.postName = postName;
        this.postResource = postResource;
    }

    @Override
    void respond(HttpServletRequest req, HttpServletResponse resp) {
        Optional<Post> postOptional = postService.findPostByName(postName);
        if (!postOptional.isPresent()) {
            respondWithError(resp, HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Post post = postOptional.get();
        servePostResource(resp, post);
    }

    private void servePostResource(HttpServletResponse resp, Post post) {
        Path resourcePath = getPostFilePath(configService, post, postResource);
        String contentType = fileService.getFileContentType(resourcePath);
        if (contentType.isEmpty() || !fileService.isFileRegularAndReadable(resourcePath)) {
            respondWithError(resp, HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        resp.setContentType(contentType);
        try {
            resp.setContentLengthLong(fileService.getFileSize(resourcePath));
            fileService.dumpFileToStream(resourcePath, resp.getOutputStream());
        } catch (IOException ex ){
            logger.error("Failed to serve resource '{}' from post {}", resourcePath, post, ex);
            respondWithError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

}
