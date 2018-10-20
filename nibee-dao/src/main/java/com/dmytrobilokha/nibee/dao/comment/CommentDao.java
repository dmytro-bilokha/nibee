package com.dmytrobilokha.nibee.dao.comment;

import com.dmytrobilokha.nibee.data.Comment;
import org.apache.ibatis.annotations.Param;
import org.mybatis.cdi.Mapper;

import java.util.List;

@Mapper
public interface CommentDao {

    int insert(Comment comment);

    int countPostComments(@Param("postId") Long postId, @Param("commentId") Long commentId);

    List<Comment> findCommentByPostId(Long postId);

}
