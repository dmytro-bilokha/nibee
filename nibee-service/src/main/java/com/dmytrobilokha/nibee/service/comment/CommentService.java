package com.dmytrobilokha.nibee.service.comment;

import com.dmytrobilokha.nibee.data.Comment;

public interface CommentService {

    Comment createAndSave(Long postId, String authorNickname, String content) throws CommentCreationException;

}
