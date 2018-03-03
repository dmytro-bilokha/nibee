package com.dmytrobilokha.nibee.service.post;

import com.dmytrobilokha.nibee.data.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Optional<Post> findPostByName(String name);

    Optional<String> findPostPathByName(String name);

    List<Post> findPostByTagId(long id);

}
