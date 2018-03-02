package com.dmytrobilokha.nibee.service.post;

import com.dmytrobilokha.nibee.dao.post.PostDao;
import com.dmytrobilokha.nibee.data.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Stateless
public class PostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);

    private PostDao postDao;

    public PostService() {
        //EJB spec required constructor
    }

    @Inject
    public PostService(PostDao postDao) {
        this.postDao = postDao;
    }

    public Optional<Post> findPostByName(String name) {
        List<Post> posts = postDao.findPostByName(name);
        if (posts.isEmpty()) {
            return Optional.empty();
        }
        if (posts.size() > 1) {
            LOGGER.warn("For name={} found more than one Post: {} this should never happen", name, posts);
        }
        return Optional.of(posts.get(0));
    }

    public List<Post> findPostByTagId(long id) {
        return postDao.findPostByTagId(id);
    }

}
