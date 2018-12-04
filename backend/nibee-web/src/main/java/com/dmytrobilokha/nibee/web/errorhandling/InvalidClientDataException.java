package com.dmytrobilokha.nibee.web.errorhandling;

import java.util.List;

public class InvalidClientDataException extends Exception {

    private final StatusMessage statusMessage;

    public InvalidClientDataException(List<String> messages) {
        super(messages.toString());
        this.statusMessage = new StatusMessage(StatusMessage.Code.CLIENT_ERROR, messages);
    }

    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

}
