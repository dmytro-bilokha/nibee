package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.post.PostService;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Dependent
public class BlogResponseFactory {

    private static final Pattern REMOVE_SLASHES_PATTERN = Pattern.compile("(^/+)|(/+$)");
    private static final Pattern CHECK_PATTERN = Pattern.compile("(/{2,})|(\\.{2,})|([^0-9a-zA-Z\\./-])");

    private final ConfigService configService;
    private final PostService postService;
    private final FileService fileService;

    @Inject
    BlogResponseFactory(ConfigService configService, PostService postService, FileService fileService) {
        this.configService = configService;
        this.postService = postService;
        this.fileService = fileService;
    }

    BlogResponse createResponse(HttpServletRequest req) {
        String requestedPath = getSanitizedServletPath(req);
        if (requestedPath == null) {
            return new NotFoundBlogResponse();
        }
        String[] postNameResourceArray = requestedPath.split("/", 2);
        if (postNameResourceArray.length == 0 || postNameResourceArray[0].isEmpty()) {
            return new WelcomeBlogResponse();
        }
        String postName = postNameResourceArray[0];
        if (postNameResourceArray.length == 1) {
            return new PostEntryBlogResponse(configService, postService, fileService, postName);
        }
        if (postNameResourceArray.length >= 2 && !postNameResourceArray[1].isEmpty()) {
            String postResource = postNameResourceArray[1];
            return new PostResourceBlogResponse(configService, postService, fileService, postName, postResource);
        }
        return new NotFoundBlogResponse();
    }

    private String getSanitizedServletPath(HttpServletRequest req) {
        String servletPath = req.getServletPath();
        String sanitizedPath = REMOVE_SLASHES_PATTERN.matcher(servletPath).replaceAll("");
        if (CHECK_PATTERN.matcher(sanitizedPath).find()) {
            return null;
        }
        return sanitizedPath.toLowerCase();
    }

}
