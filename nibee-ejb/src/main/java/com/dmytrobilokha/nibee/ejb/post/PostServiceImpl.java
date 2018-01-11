package com.dmytrobilokha.nibee.ejb.post;

import com.dmytrobilokha.nibee.ejb.PostService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class PostServiceImpl implements PostService {

    @Inject
    private PostRepository postRepository;

    public PostServiceImpl() {
        //EJB spec required constructor
    }

    public List<String> getNames() {
        return postRepository.getNames();
    }

}
