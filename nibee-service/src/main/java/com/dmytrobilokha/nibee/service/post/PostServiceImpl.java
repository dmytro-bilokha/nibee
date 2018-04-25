package com.dmytrobilokha.nibee.service.post;

import com.dmytrobilokha.nibee.dao.post.PostDao;
import com.dmytrobilokha.nibee.data.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
public class PostServiceImpl implements PostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);

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
        List<Post> posts = postDao.findPostByName(name);
        if (posts.isEmpty()) {
            return Optional.empty();
        }
        if (posts.size() > 1) {
            LOGGER.warn("For name='{}' found more than one Post: {} this should never happen", name, posts);
        }
        return Optional.of(posts.get(0));
    }

    @Override
    public Optional<String> findPostPathByName(String name) {
        List<String> paths = postDao.findPostPathByName(name);
        if (paths.isEmpty()) {
            return Optional.empty();
        }
        if (paths.size() > 1) {
            LOGGER.warn("For name='{}' found more than one post path: {} this should never happen", name, paths);
        }
        return Optional.of(paths.get(0));
    }

    @Override
    public List<Post> findPostByTagId(long id) {
        return postDao.findPostByTagId(id);
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
