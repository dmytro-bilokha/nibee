package com.dmytrobilokha.nibee.dao.comment;

import com.dmytrobilokha.nibee.data.Comment;
import org.mybatis.cdi.Mapper;

import java.util.List;

@Mapper
public interface CommentDao {

    int insert(Comment comment);
    List<Comment> findCommentByPostId(Long postId);

}
