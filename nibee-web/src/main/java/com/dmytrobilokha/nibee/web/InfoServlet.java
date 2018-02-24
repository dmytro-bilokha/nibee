package com.dmytrobilokha.nibee.web;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.ejb.config.ConfigProperty;
import com.dmytrobilokha.nibee.ejb.post.PostService;
import com.dmytrobilokha.nibee.ejb.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = "/")
public class InfoServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfoServlet.class);
    private static final Pattern SANITIZE_PATTERN = Pattern.compile("(^/+)|(/+$)|([^0-9a-zA-Z/-])");
    private static final Pattern MULTI_SLASH_PATTERN = Pattern.compile("/{2,}");

    private final ConfigService configService;
    private final PostService postService;

    @Inject
    public InfoServlet(ConfigService configService, PostService postService) {
        this.configService = configService;
        this.postService = postService;
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
        req.setAttribute("name", postName);
        Optional<Post> postOptional = postService.findPostByName(postName);
        Post post;
        if (postOptional.isPresent()) {
            post = postOptional.get();
            req.setAttribute("post", post);
        } else {
            //TODO send 404 here
            NavigablePage.POST_NOT_FOUND.forwardTo(req, resp);
            return;
        }
        if (postNameResourceArray.length == 2 && !postNameResourceArray[1].isEmpty()) {
            servePostResource(req, resp, post, postNameResourceArray[1]);
            return;
        }
        NavigablePage.POST.forwardTo(req, resp);
    }

    private String getSanitizedServletPath(HttpServletRequest req) {
        String servletPath = req.getServletPath();
        String pathWithoutUnwantedChars = SANITIZE_PATTERN.matcher(servletPath).replaceAll("");
        String pathWithSingleSlashesOnly = MULTI_SLASH_PATTERN.matcher(pathWithoutUnwantedChars).replaceAll("/");
        return pathWithSingleSlashesOnly.toLowerCase();
    }

    private void servePostResource(HttpServletRequest req, HttpServletResponse resp, Post post, String postResource) {
        Path resourcePath = Paths.get(configService.getAsString(ConfigProperty.POSTS_ROOT) + '/'
                + post.getPath() + '/' + postResource);
        if (!Files.isReadable(resourcePath)) {
            //TODO send 404 here and show the url
            NavigablePage.POST_NOT_FOUND.forwardTo(req, resp);
            return;
        }
        //TODO setContentType and setContentLength here
        byte[] buffer = new byte[8 * 1024];
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        try (SeekableByteChannel channel = Files.newByteChannel(resourcePath, StandardOpenOption.READ)) {
            OutputStream out = resp.getOutputStream();
            for (int length = 0; (length = channel.read(byteBuffer)) != -1;) {
                out.write(buffer, 0, length);
                byteBuffer.clear();
            }
        } catch (IOException ex ){
            LOGGER.error("Failed to server resource {} from post {}", resourcePath, post);
            //TODO send 500 here
        }
    }

}
