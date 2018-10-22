package com.dmytrobilokha.nibee.web.blog;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/")
public class BlogServlet extends HttpServlet {

    private final BlogResponseFactory blogResponseFactory;

    @Inject
    public BlogServlet(BlogResponseFactory blogResponseFactory) {
        this.blogResponseFactory = blogResponseFactory;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        BlogResponse blogResponse = blogResponseFactory.createResponse(req);
        blogResponse.respond(req, resp);
    }

}
