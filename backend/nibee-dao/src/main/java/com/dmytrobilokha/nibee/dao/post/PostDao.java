package com.dmytrobilokha.nibee.dao.post;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.data.PostWithTags;
import org.apache.ibatis.annotations.Param;
import org.mybatis.cdi.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostDao {

    PostWithTags findPostByName(String name);

    String findPostPathByName(String name);

    List<PostWithTags> findPostAfter(@Param("dateTime") LocalDateTime dateTime, @Param("tagId") Long tagId
            , @Param("limit") int limit);

    List<PostWithTags> findPostBefore(@Param("dateTime") LocalDateTime dateTime, @Param("tagId") Long tagId
            , @Param("limit") int limit);

    int countPostsById(Long id);

    List<Post> findPostDuplicates(Post post);

    int createPost(Post post);

}
