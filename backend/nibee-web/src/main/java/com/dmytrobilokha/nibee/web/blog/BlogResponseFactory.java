package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.file.FileService;
import com.dmytrobilokha.nibee.service.post.PostService;
import com.dmytrobilokha.nibee.web.comment.CommentsModelCreator;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Dependent
public class BlogResponseFactory {

    private static final Pattern REMOVE_SLASHES_PATTERN = Pattern.compile("(^/+)|(/+$)");
    private static final Pattern CHECK_PATTERN = Pattern.compile("(/{2,})|(\\.{2,})|([^0-9a-zA-Z\\./-])");
    private static final int POST_ENTRY_REQUEST_LENGTH = 1;
    private static final int RESOURCE_MIN_REQUEST_LENGTH = 2;

    private final ConfigService configService;
    private final PostService postService;
    private final FileService fileService;
    private final CommentsModelCreator commentsModelCreator;

    @Inject
    BlogResponseFactory(ConfigService configService, PostService postService
            , FileService fileService, CommentsModelCreator commentsModelCreator) {
        this.configService = configService;
        this.postService = postService;
        this.fileService = fileService;
        this.commentsModelCreator = commentsModelCreator;
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
        if (postNameResourceArray.length == POST_ENTRY_REQUEST_LENGTH) {
            return new PostEntryBlogResponse(configService, postService, fileService, commentsModelCreator, postName);
        }
        if (postNameResourceArray.length >= RESOURCE_MIN_REQUEST_LENGTH
                && !postNameResourceArray[1].isEmpty()) {
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
        return sanitizedPath;
    }

}
