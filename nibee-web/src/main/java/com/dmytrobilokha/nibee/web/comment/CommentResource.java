package com.dmytrobilokha.nibee.web.comment;

import com.dmytrobilokha.nibee.data.Comment;
import com.dmytrobilokha.nibee.service.comment.CommentCreationException;
import com.dmytrobilokha.nibee.service.comment.CommentService;
import com.dmytrobilokha.nibee.web.NavigablePage;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("comments")
public class CommentResource {

    private final CommentService commentService;

    @Inject
    public CommentResource(CommentService commentService) {
        this.commentService = commentService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Comment create(
            @FormParam("postId") Long postId
            , @FormParam("authorNickname") String authorNickname
            , @FormParam("content") String content
    ) throws CommentCreationException {
        return commentService.createAndSave(postId, authorNickname, content);
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("{postId}")
    public void fetch(@Context HttpServletRequest request
                      , @Context HttpServletResponse response
                      , @PathParam("postId") Long postId) {
        List<Comment> comments = commentService.fetchPostComments(postId);
        CommentsModel model = new CommentsModel(comments);
        model.putInRequest(request);
        NavigablePage.COMMENTS.forwardTo(request, response);
    }

}
