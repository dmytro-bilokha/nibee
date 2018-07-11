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

@Path("comments")
public class CommentResource {

    private final CommentService commentService;
    private final CommentsModelCreator commentsModelCreator;

    @Inject
    public CommentResource(CommentService commentService, CommentsModelCreator commentsModelCreator) {
        this.commentService = commentService;
        this.commentsModelCreator = commentsModelCreator;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Long create(
            @FormParam("postId") Long postId
            , @FormParam("authorNickname") String authorNickname
            , @FormParam("content") String content
    ) throws CommentCreationException {
        Comment comment = commentService.createAndSave(postId, authorNickname, content);
        return comment.getId();
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("{postId}")
    public void fetch(@Context HttpServletRequest request
                      , @Context HttpServletResponse response
                      , @PathParam("postId") Long postId) {
        commentsModelCreator.createAndPutInRequest(postId, request);
        NavigablePage.COMMENTS.forwardTo(request, response);
    }

}
