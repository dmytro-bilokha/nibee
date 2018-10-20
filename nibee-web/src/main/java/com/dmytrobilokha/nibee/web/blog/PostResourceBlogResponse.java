package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.post.PostService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;

class PostResourceBlogResponse extends BlogResponse {

    final String postName;
    final String postResource;
    private final ConfigService configService;
    private final PostService postService;
    private final FileService fileService;

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
        String postBase = postService.findPostPathByName(postName);
        if (postBase == null) {
            respondWithError(resp, HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        servePostResource(resp, postBase);
    }

    private void servePostResource(HttpServletResponse resp, String postBase) {
        Path resourcePath = getPostFilePath(configService, postBase, postResource);
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
            logger.error("Failed to serve resource '{}' from post {}", resourcePath, postBase, ex);
            respondWithError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

}
