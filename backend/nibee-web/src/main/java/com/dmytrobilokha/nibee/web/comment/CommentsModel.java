package com.dmytrobilokha.nibee.web.comment;

import com.dmytrobilokha.nibee.web.InRequestPuttable;

import java.util.Collections;
import java.util.List;

public class CommentsModel implements InRequestPuttable {

    private final Long postId;
    private final boolean showNewCommentForm;
    private final List<CommentModel> comments;

    CommentsModel(Long postId, boolean showNewCommentForm, List<CommentModel> comments) {
        this.postId = postId;
        this.showNewCommentForm = showNewCommentForm;
        this.comments = Collections.unmodifiableList(comments);
    }

    public Long getPostId() {
        return postId;
    }

    public boolean isShowNewCommentForm() {
        return showNewCommentForm;
    }

    public List<CommentModel> getComments() {
        return comments;
    }

}
