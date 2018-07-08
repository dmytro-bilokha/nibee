package com.dmytrobilokha.nibee.web.comment;

import com.dmytrobilokha.nibee.data.Comment;
import com.dmytrobilokha.nibee.service.comment.CommentCreationException;
import com.dmytrobilokha.nibee.service.comment.CommentService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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

}
