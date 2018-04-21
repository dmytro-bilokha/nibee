package com.dmytrobilokha.nibee.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public enum NavigablePage {

    WELCOME("/WEB-INF/jsp/welcome.jspx")
    , HOME("/WEB-INF/home")
    , POST("/WEB-INF/jsp/postPage.jspx")
    , CONFIG_REPORT("/WEB-INF/jsp/admin/configReport.jspx")
    ;

    private final String location;

    NavigablePage(String location) {
        this.location = location;
    }

    public void forwardTo(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher(location).forward(request, response);
        } catch (ServletException | IOException e) {
            throw new IllegalStateException("The page '" + location + "' has thrown an exception", e);
        }
    }

}
