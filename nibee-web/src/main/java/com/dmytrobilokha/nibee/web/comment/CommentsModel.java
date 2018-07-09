package com.dmytrobilokha.nibee.web.comment;

import com.dmytrobilokha.nibee.data.Comment;
import com.dmytrobilokha.nibee.web.InRequestPuttable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CommentsModel implements InRequestPuttable {

    private final Collection<Comment> comments;

    CommentsModel(Collection<Comment> comments) {
        this.comments = Collections.unmodifiableCollection(new ArrayList<>(comments));
    }

    public Collection<Comment> getComments() {
        return comments;
    }

}
