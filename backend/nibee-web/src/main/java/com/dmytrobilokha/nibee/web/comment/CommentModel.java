package com.dmytrobilokha.nibee.web.comment;

import com.dmytrobilokha.nibee.data.Comment;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CommentModel {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/uuuu' at 'HH:mm", Locale.ENGLISH);

    private final Long id;
    private final String authorNickname;
    private final String content;
    private final String createdOn;
    private final int depthLevel;

    CommentModel(Comment comment, int depthLevel) {
        this.id = comment.getId();
        this.authorNickname = comment.getAuthorNickname();
        this.content = comment.getContent();
        this.createdOn = DATE_TIME_FORMATTER.format(comment.getCreatedOn());
        this.depthLevel = depthLevel;
    }

    public Long getId() {
        return id;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public int getDepthLevel() {
        return depthLevel;
    }

}
