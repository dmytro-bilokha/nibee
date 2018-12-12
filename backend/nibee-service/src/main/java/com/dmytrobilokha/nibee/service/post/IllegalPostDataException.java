package com.dmytrobilokha.nibee.service.post;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class IllegalPostDataException extends Exception {

    IllegalPostDataException(String message, Exception ex) {
        super(message, ex);
    }

    IllegalPostDataException(String message) {
        super(message);
    }

}
