package com.dmytrobilokha.nibee.web.home;

import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.post.PostService;
import com.dmytrobilokha.nibee.web.NavigablePage;
import com.dmytrobilokha.nibee.web.param.InvalidParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(urlPatterns = "/WEB-INF/home")
public class HomeServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeServlet.class);

    private final PostService postService;
    private final ConfigService configService;

    @Inject
    public HomeServlet(PostService postService, ConfigService configService) {
        this.postService = postService;
        this.configService = configService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        LocalDateTime beforeValue;
        LocalDateTime afterValue;
        Long tagIdValue;
        try {
            beforeValue = BrowsePostsModel.extractBeforeParam(req);
            afterValue = BrowsePostsModel.extractAfterParam(req);
            tagIdValue = BrowsePostsModel.extractTagIdParam(req);
        } catch (InvalidParamException ex) {
            sendBadRequestError(req, resp, ex);
            return;
        }
        int headlinersPerPage = configService.getAsInt(ConfigProperty.HEADLINERS_PER_PAGE);
        BrowsePostsModel model = new BrowsePostsModelBuilder(postService, headlinersPerPage)
                .before(beforeValue)
                .after(afterValue)
                .tagId(tagIdValue)
                .build();
        if (model == null) {
            NavigablePage.NO_POSTS.forwardTo(req, resp);
            return;
        }
        model.putInRequest(req);
        NavigablePage.BROWSE_POSTS.forwardTo(req, resp);
    }

    private void sendBadRequestError(HttpServletRequest req, HttpServletResponse resp
            , InvalidParamException invalidParamException) {
        invalidParamException.putInRequest(req);
        try {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException ex) {
            LOGGER.error("Got exception when tried to send 400 Bad Request error", ex);
        }
    }

}
