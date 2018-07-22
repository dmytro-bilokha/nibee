package com.dmytrobilokha.nibee.web.comment;

import com.dmytrobilokha.nibee.web.InRequestPuttable;

import java.util.Collections;
import java.util.List;

public class CommentsModel implements InRequestPuttable {

    private final Long postId;
    private final List<CommentModel> comments;

    CommentsModel(Long postId, List<CommentModel> comments) {
        this.postId = postId;
        this.comments = Collections.unmodifiableList(comments);
    }

    public Long getPostId() {
        return postId;
    }

    public List<CommentModel> getComments() {
        return comments;
    }

}
