package com.dmytrobilokha.nibee.service.post;

import com.dmytrobilokha.nibee.dao.post.PostDao;
import com.dmytrobilokha.nibee.data.Post;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

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
    public Post findPostByName(String name) {
        return postDao.findPostByName(name);
    }

    @Override
    public String findPostPathByName(String name) {
        return postDao.findPostPathByName(name);
    }

    @Override
    public List<Post> findPostAfter(LocalDateTime dateTime, int limit) {
        return postDao.findPostAfter(dateTime, null, limit);
    }

    @Override
    public List<Post> findPostBefore(LocalDateTime dateTime, int limit) {
        return postDao.findPostBefore(dateTime, null, limit);
    }

    @Override
    public List<Post> findPostAfterFilteredByTag(LocalDateTime dateTime, Long tagId, int limit) {
        return postDao.findPostAfter(dateTime, tagId, limit);
    }

    @Override
    public List<Post> findPostBeforeFilteredByTag(LocalDateTime dateTime, Long tagId, int limit) {
        return postDao.findPostBefore(dateTime, tagId, limit);
    }

    @Override
    public boolean doesPostExist(Long postId) {
        return postDao.countPostsById(postId) > 0;
    }

}
