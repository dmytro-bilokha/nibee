package com.dmytrobilokha.nibee.dao.post;

import com.dmytrobilokha.nibee.data.Post;
import org.mybatis.cdi.Mapper;

import java.util.List;

@Mapper
public interface PostRepository {

    List<Post> findPostByName(String name);
    List<Post> findPostByTagId(long tagId);

}
