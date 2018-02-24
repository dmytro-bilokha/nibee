package com.dmytrobilokha.nibee.web;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.post.PostService;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = "/")
public class InfoServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfoServlet.class);
    private static final Pattern SANITIZE_PATTERN = Pattern.compile("(^/+)|(/+$)|(/{2,})|(\\.{2,})|([^0-9a-zA-Z\\./-])");
    private final ConfigService configService;
    private final PostService postService;
    private final FileService fileService;

    @Inject
    public InfoServlet(ConfigService configService, PostService postService, FileService fileService) {
        this.configService = configService;
        this.postService = postService;
        this.fileService = fileService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String requestedPath = getSanitizedServletPath(req);
        String[] postNameResourceArray = requestedPath.split("/", 2);
        if (postNameResourceArray.length == 0 || postNameResourceArray[0].isEmpty()) {
            NavigablePage.WELCOME.forwardTo(req, resp);
            return;
        }
        String postName = postNameResourceArray[0];
        Optional<Post> postOptional = postService.findPostByName(postName);
        if (!postOptional.isPresent()) {
            respondWithError(resp, HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Post post = postOptional.get();
        if (postNameResourceArray.length == 1) {
            req.setAttribute("post", post);
            NavigablePage.POST.forwardTo(req, resp);
            return;
        }
        if (postNameResourceArray.length == 2 && !postNameResourceArray[1].isEmpty()) {
            servePostResource(resp, post, postNameResourceArray[1]);
            return;
        }
        respondWithError(resp, HttpServletResponse.SC_NOT_FOUND);
    }

    private void respondWithError(HttpServletResponse resp, int errorCode) {
        try {
            resp.sendError(errorCode);
        } catch (IOException ex) {
            LOGGER.error("Got exception when tried to send errorCode={}", errorCode, ex);
        }
    }

    private String getSanitizedServletPath(HttpServletRequest req) {
        String servletPath = req.getServletPath();
        String pathWithoutUnwantedChars = SANITIZE_PATTERN.matcher(servletPath).replaceAll("");
        return pathWithoutUnwantedChars.toLowerCase();
    }

    private void servePostResource(HttpServletResponse resp, Post post, String postResource) {
        Path resourcePath = Paths.get(configService.getAsString(ConfigProperty.POSTS_ROOT) + '/'
                + post.getPath() + '/' + postResource);
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
            LOGGER.error("Failed to serve resource {} from post {}", resourcePath, post, ex);
            respondWithError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

}
