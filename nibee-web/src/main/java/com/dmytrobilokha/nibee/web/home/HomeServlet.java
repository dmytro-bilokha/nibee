package com.dmytrobilokha.nibee.web.home;

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
import java.util.Optional;

@WebServlet(urlPatterns = "/WEB-INF/home")
public class HomeServlet extends HttpServlet{

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeServlet.class);

    private final PostService postService;

    @Inject
    public HomeServlet(PostService postService) {
        this.postService = postService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        Optional<LocalDateTime> beforeValue;
        Optional<LocalDateTime> afterValue;
        Optional<Long> tagIdValue;
        try {
            beforeValue = BrowsePostsModel.extractBeforeParam(req);
            afterValue = BrowsePostsModel.extractAfterParam(req);
            tagIdValue = BrowsePostsModel.extractTagIdParam(req);
        } catch (InvalidParamException ex) {
            sendBadRequestError(req, resp, ex);
            return;
        }
        BrowsePostsModel model = new BrowsePostsModelBuilder(postService)
                .before(beforeValue.orElse(null))
                .after(afterValue.orElse(null))
                .tagId(tagIdValue.orElse(null))
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
