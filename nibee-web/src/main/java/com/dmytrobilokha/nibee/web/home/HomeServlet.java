package com.dmytrobilokha.nibee.web.home;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.service.post.PostService;
import com.dmytrobilokha.nibee.web.NavigablePage;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = "/WEB-INF/home")
public class HomeServlet extends HttpServlet{

    private static final LocalDateTime THE_END_OF_TIME = LocalDateTime.of(3000, 1, 1, 0, 1);
    private static final int HEADLINERS_PER_PAGE = 2;

    private final PostService postService;

    @Inject
    public HomeServlet(PostService postService) {
        this.postService = postService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String beforeParam = req.getParameter("before");
        Optional<LocalDateTime> beforeValue = BrowsePostsModel.parseDateTimeParam(beforeParam);
        List<Post> posts;
        boolean backPossible;
        boolean forwardPossible;
        if (beforeValue.isPresent()) {
            posts = postService.findPostBefore(beforeValue.get(), null, HEADLINERS_PER_PAGE + 1);
            backPossible = true;
        } else {
            posts = postService.findPostBefore(THE_END_OF_TIME, null, HEADLINERS_PER_PAGE + 1);
            backPossible = false;
        }
        forwardPossible = posts.size() > HEADLINERS_PER_PAGE;
        if (posts.size() > HEADLINERS_PER_PAGE) {
            posts = posts.subList(0, HEADLINERS_PER_PAGE);
        }
        BrowsePostsModel model = new BrowsePostsModel(posts, backPossible, forwardPossible);
        model.putInRequest(req);
        NavigablePage.BROWSE_POSTS.forwardTo(req, resp);
    }

}
