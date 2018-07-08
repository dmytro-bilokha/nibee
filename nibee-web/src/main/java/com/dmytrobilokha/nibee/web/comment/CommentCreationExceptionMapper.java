package com.dmytrobilokha.nibee.web.comment;

import com.dmytrobilokha.nibee.service.comment.CommentCreationException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CommentCreationExceptionMapper implements ExceptionMapper<CommentCreationException> {

    @Override
    public Response toResponse(CommentCreationException exception) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .entity(exception.getMessage())
                .build();
    }

}
