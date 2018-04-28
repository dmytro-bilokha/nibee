package com.dmytrobilokha.nibee.web.home;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.data.Tag;
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
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = "/WEB-INF/home")
public class HomeServlet extends HttpServlet{

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeServlet.class);
    private static final LocalDateTime THE_END_OF_TIME = LocalDateTime.of(3000, 1, 1, 0, 1);
    private static final int HEADLINERS_PER_PAGE = 2;

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
        BrowsePostsModel model;
        if (beforeValue.isPresent()) {
            model = buildModelWithBefore(beforeValue.get(), tagIdValue);
        } else if(afterValue.isPresent()) {
            model = buildModelWithAfter(afterValue.get(), tagIdValue);
        } else {
            model = buildModelForFirstPage(tagIdValue);
        }
        if (model == null) {
            NavigablePage.NO_POSTS.forwardTo(req, resp);
            return;
        }
        model.putInRequest(req);
        NavigablePage.BROWSE_POSTS.forwardTo(req, resp);
    }

    private BrowsePostsModel buildModelForFirstPage(Optional<Long> tagIdValue) {
        List<Post> posts = postService.findPostBefore(THE_END_OF_TIME, tagIdValue, HEADLINERS_PER_PAGE + 1);
        if (posts.isEmpty()) {
            return null;
        }
        BrowsePostsModel.NavigationType navigationType = BrowsePostsModel.NavigationType.NO;
        if (posts.size() > HEADLINERS_PER_PAGE) {
            navigationType = BrowsePostsModel.NavigationType.FORWARD;
            posts = posts.subList(0, HEADLINERS_PER_PAGE);
        }
        return createBrowsePostsModel(posts, navigationType, tagIdValue);
    }

    private BrowsePostsModel buildModelWithBefore(LocalDateTime beforeDateTime, Optional<Long> tagIdValue) {
        List<Post> posts = postService.findPostBefore(beforeDateTime, tagIdValue, HEADLINERS_PER_PAGE + 1);
        if (posts.isEmpty()) {
            return null;
        }
        BrowsePostsModel.NavigationType navigationType = BrowsePostsModel.NavigationType.BACK;
        if (posts.size() > HEADLINERS_PER_PAGE) {
            navigationType = BrowsePostsModel.NavigationType.BACK_AND_FORWARD;
            posts = posts.subList(0, HEADLINERS_PER_PAGE);
        }
        return createBrowsePostsModel(posts, navigationType, tagIdValue);
    }

    private BrowsePostsModel buildModelWithAfter(LocalDateTime afterDateTime, Optional<Long> tagIdValue) {
        List<Post> posts = postService.findPostAfter(afterDateTime, tagIdValue, HEADLINERS_PER_PAGE + 1);
        if (posts.isEmpty()) {
            return null;
        }
        BrowsePostsModel.NavigationType navigationType = BrowsePostsModel.NavigationType.FORWARD;
        if (posts.size() > HEADLINERS_PER_PAGE) {
            navigationType = BrowsePostsModel.NavigationType.BACK_AND_FORWARD;
            posts = posts.subList(1, posts.size());
        }
        return createBrowsePostsModel(posts, navigationType, tagIdValue);
    }

    private BrowsePostsModel createBrowsePostsModel(List<Post> posts, BrowsePostsModel.NavigationType navigationType
        , Optional<Long> tagIdValue) {
        if (tagIdValue.isPresent()) {
            Long tagId = tagIdValue.get();
            Optional<Tag> tagValue = posts.get(0).getTags().stream()
                    .filter(t -> tagId.equals(t.getId()))
                    .findFirst();
            if (tagValue.isPresent()) {
                return new BrowsePostsModel(posts, navigationType, tagValue.get());
            }
        }
        return new BrowsePostsModel(posts, navigationType);
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
