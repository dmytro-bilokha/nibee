package com.dmytrobilokha.nibee.ejb.post;

import com.dmytrobilokha.nibee.dao.post.PostRepository;

import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class PostService {

    private PostRepository postRepository;
    private EJBContext ejbContext;

    public PostService() {
        //EJB spec required constructor
    }

    @Inject
    public PostService(PostRepository postRepository, EJBContext ejbContext) {
        this.postRepository = postRepository;
        this.ejbContext = ejbContext;
    }

    public List<String> getNames() {
        return postRepository.getNames();
    }

}
