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

@WebServlet(urlPatterns = "/WEB-INF/home")
public class HomeServlet extends HttpServlet{

    private static final LocalDateTime THE_BEGINNING = LocalDateTime.of(2000, 1, 1, 0, 1);
    private static final int HEADLINERS_PER_PAGE = 1;

    private final PostService postService;

    @Inject
    public HomeServlet(PostService postService) {
        this.postService = postService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        List<Post> posts = postService.findPostAfter(THE_BEGINNING, null, HEADLINERS_PER_PAGE + 1);
        boolean backPossible = false;
        boolean forwardPossible = posts.size() > HEADLINERS_PER_PAGE;
        if (posts.size() > HEADLINERS_PER_PAGE) {
            posts = posts.subList(0, HEADLINERS_PER_PAGE);
        }
        BrowsePostsModel model = new BrowsePostsModel(posts, backPossible, forwardPossible);
        model.putInRequest(req);
        NavigablePage.BROWSE_POSTS.forwardTo(req, resp);
    }

}
