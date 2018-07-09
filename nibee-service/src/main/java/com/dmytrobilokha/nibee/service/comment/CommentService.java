package com.dmytrobilokha.nibee.service.comment;

import com.dmytrobilokha.nibee.data.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> fetchPostComments(Long postId);

    Comment createAndSave(Long postId, String authorNickname, String content) throws CommentCreationException;

}
