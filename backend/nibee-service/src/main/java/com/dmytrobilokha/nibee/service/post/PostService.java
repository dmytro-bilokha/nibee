package com.dmytrobilokha.nibee.service.post;

import com.dmytrobilokha.nibee.data.Post;
import com.dmytrobilokha.nibee.data.PostWithTags;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface PostService {

    PostWithTags findPostByName(String name);

    String findPostPathByName(String name);

    List<PostWithTags> findPostAfter(LocalDateTime dateTime, int limit);

    List<PostWithTags> findPostBefore(LocalDateTime dateTime, int limit);

    List<PostWithTags> findPostAfterFilteredByTag(LocalDateTime dateTime, Long tagId, int limit);

    List<PostWithTags> findPostBeforeFilteredByTag(LocalDateTime dateTime, Long tagId, int limit);

    boolean doesPostExist(Long postId);

    void createPost(
            InputStream postFileInputStream
            , Post post
            , Set<Long> tagIds
    ) throws IllegalPostDataException;

}
