package com.dmytrobilokha.nibee.service.post;

import com.dmytrobilokha.nibee.dao.post.PostDao;
import com.dmytrobilokha.nibee.data.Post;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
public class PostServiceImpl implements PostService {

    private PostDao postDao;

    public PostServiceImpl() {
        //EJB spec required constructor
    }

    @Inject
    public PostServiceImpl(PostDao postDao) {
        this.postDao = postDao;
    }

    @Override
    public Optional<Post> findPostByName(String name) {
        Post post = postDao.findPostByName(name);
        return Optional.ofNullable(post);
    }

    @Override
    public Optional<String> findPostPathByName(String name) {
        String path = postDao.findPostPathByName(name);
        return Optional.ofNullable(path);
    }

    @Override
    public List<Post> findPostAfter(LocalDateTime dateTime, Optional<Long> tagId, int limit) {
        return postDao.findPostAfter(dateTime, tagId.orElse(null), limit);
    }

    @Override
    public List<Post> findPostBefore(LocalDateTime dateTime, Optional<Long> tagId, int limit) {
        return postDao.findPostBefore(dateTime, tagId.orElse(null), limit);
    }

}
