package com.dmytrobilokha.nibee.ejb.post;

import com.dmytrobilokha.nibee.dao.post.PostRepository;

import javax.annotation.Resource;
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
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<String> getNames() {
        return postRepository.getNames();
    }

    public void changeFirstName(String name) {
        postRepository.changeFirstName(name);
        if (name.startsWith("F")) {
            ejbContext.setRollbackOnly();
        }
    }

    @Resource
    public void setEjbContext(EJBContext ejbContext) {
        this.ejbContext = ejbContext;
    }
}
