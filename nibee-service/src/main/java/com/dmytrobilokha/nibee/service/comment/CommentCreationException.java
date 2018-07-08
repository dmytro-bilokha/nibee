package com.dmytrobilokha.nibee.service.comment;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class CommentCreationException extends Exception {

    CommentCreationException(String message) {
        super(message);
    }

}
