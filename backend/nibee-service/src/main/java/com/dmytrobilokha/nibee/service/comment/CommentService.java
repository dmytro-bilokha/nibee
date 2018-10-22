package com.dmytrobilokha.nibee.service.comment;

import com.dmytrobilokha.nibee.data.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> fetchPostComments(Long postId);

    boolean doesPostCommentExist(Long postId, Long commentId);

    Comment createAndSave(Long postId, Long parentCommentId, String authorNickname, String content)
            throws CommentCreationException;

}
