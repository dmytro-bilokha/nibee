package com.dmytrobilokha.nibee.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public enum NavigablePage {

    INFO("info.jspx");

    private final String location;

    NavigablePage(String relativeLocation) {
        this.location = "/WEB-INF/jsp/" + relativeLocation;
    }

    public void forwardTo(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher(location).forward(request, response);
        } catch (ServletException | IOException e) {
            throw new IllegalStateException("The page '" + location + "' has thrown an exception", e);
        }
    }

}
