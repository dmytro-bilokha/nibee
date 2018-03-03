package com.dmytrobilokha.nibee.web.blog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class NotFoundBlogResponse extends BlogResponse {

    @Override
    void respond(HttpServletRequest req, HttpServletResponse resp) {
        respondWithError(resp, HttpServletResponse.SC_NOT_FOUND);
    }

}
