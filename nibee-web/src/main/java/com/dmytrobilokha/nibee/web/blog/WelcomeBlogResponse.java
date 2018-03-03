package com.dmytrobilokha.nibee.web.blog;

import com.dmytrobilokha.nibee.web.NavigablePage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class WelcomeBlogResponse extends BlogResponse {

    @Override
    void respond(HttpServletRequest req, HttpServletResponse resp) {
        NavigablePage.WELCOME.forwardTo(req, resp);
    }

}
