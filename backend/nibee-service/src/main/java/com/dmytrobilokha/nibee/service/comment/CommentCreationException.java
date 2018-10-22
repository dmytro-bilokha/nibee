package com.dmytrobilokha.nibee.service.comment;

import javax.ejb.ApplicationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationException(rollback = true)
public class CommentCreationException extends Exception {

    private final List<String> messages;

    CommentCreationException(List<String> messages) {
        super(messages.stream().collect(Collectors.joining("; ")));
        this.messages = new ArrayList<>(messages);
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

}
