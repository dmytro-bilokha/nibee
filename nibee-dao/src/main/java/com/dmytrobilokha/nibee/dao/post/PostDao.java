package com.dmytrobilokha.nibee.dao.post;

import com.dmytrobilokha.nibee.data.Post;
import org.apache.ibatis.annotations.Param;
import org.mybatis.cdi.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostDao {

    List<Post> findPostByName(String name);

    List<String> findPostPathByName(String name);

    List<Post> findPostByTagId(long tagId);

    List<Post> findPostAfter(@Param("dateTime") LocalDateTime dateTime, @Param("tagId") Long tagId
            , @Param("limit") int limit);

    List<Post> findPostBefore(@Param("dateTime") LocalDateTime dateTime, @Param("tagId") Long tagId
            , @Param("limit") int limit);

}
