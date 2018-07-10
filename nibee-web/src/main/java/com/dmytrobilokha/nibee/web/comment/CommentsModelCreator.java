package com.dmytrobilokha.nibee.web.comment;

import com.dmytrobilokha.nibee.data.Comment;
import com.dmytrobilokha.nibee.service.comment.CommentService;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Dependent
public class CommentsModelCreator {

    private final CommentService commentService;

    @Inject
    public CommentsModelCreator(CommentService commentService) {
        this.commentService = commentService;
    }

    public void createAndPutInRequest(Long postId, HttpServletRequest req) {
        List<Comment> comments = commentService.fetchPostComments(postId);
        CommentsModel model = new CommentsModel(comments);
        model.putInRequest(req);
    }

}
