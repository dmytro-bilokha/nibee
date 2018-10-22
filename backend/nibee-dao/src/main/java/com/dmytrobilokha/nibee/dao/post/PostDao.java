package com.dmytrobilokha.nibee.dao.post;

import com.dmytrobilokha.nibee.data.Post;
import org.apache.ibatis.annotations.Param;
import org.mybatis.cdi.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostDao {

    Post findPostByName(String name);

    String findPostPathByName(String name);

    List<Post> findPostAfter(@Param("dateTime") LocalDateTime dateTime, @Param("tagId") Long tagId
            , @Param("limit") int limit);

    List<Post> findPostBefore(@Param("dateTime") LocalDateTime dateTime, @Param("tagId") Long tagId
            , @Param("limit") int limit);

    int countPostsById(Long id);

}
