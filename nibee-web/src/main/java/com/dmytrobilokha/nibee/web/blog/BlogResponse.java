package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract class BlogResponse {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    abstract void respond(HttpServletRequest req, HttpServletResponse resp);

    Path getPostFilePath(ConfigService configService, String postBase, String postFile) {
        return Paths.get(configService.getAsString(ConfigProperty.POSTS_ROOT) + '/'
                + postBase + '/' + postFile);
    }

    void respondWithError(HttpServletResponse resp, int errorCode) {
        try {
            resp.sendError(errorCode);
        } catch (IOException ex) {
            logger.error("Got exception when tried to send errorCode={}", errorCode, ex);
        }
    }

}
