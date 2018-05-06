package com.dmytrobilokha.nibee.web.weblog;

import javax.enterprise.context.Dependent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@Dependent
public class CookieHelper {

    String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        Optional<Cookie> cookieOptional = Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findFirst();
        if (cookieOptional.isPresent()) {
            return cookieOptional.get().getValue();
        }
        return null;
    }

    void attachCookie(HttpServletResponse response, String cookieName, String cookieValue, int expiry) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(expiry);
        response.addCookie(cookie);
    }

}
