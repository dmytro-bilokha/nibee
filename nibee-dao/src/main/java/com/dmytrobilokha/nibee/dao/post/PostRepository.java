package com.dmytrobilokha.nibee.dao.post;

import com.dmytrobilokha.nibee.data.Post;
import org.mybatis.cdi.Mapper;

import java.util.List;

@Mapper
public interface PostRepository {

    List<String> getNames();
    void changeFirstName(String name);

    List<Post> findPostByName(String name);

}
