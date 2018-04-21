package com.dmytrobilokha.nibee.web.home;

import com.dmytrobilokha.nibee.web.NavigablePage;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/WEB-INF/home")
public class HomeServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        NavigablePage.WELCOME.forwardTo(req, resp);
    }

}
