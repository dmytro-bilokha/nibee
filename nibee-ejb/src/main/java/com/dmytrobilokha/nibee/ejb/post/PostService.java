package com.dmytrobilokha.nibee.ejb.post;

import com.dmytrobilokha.nibee.dao.post.PostRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class PostService {

    @Inject
    private PostRepository postRepository;

    public PostService() {
        //EJB spec required constructor
    }

    public List<String> getNames() {
        return postRepository.getNames();
    }

}
