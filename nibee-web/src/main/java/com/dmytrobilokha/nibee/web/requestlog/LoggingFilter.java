package com.dmytrobilokha.nibee.web.requestlog;

import com.dmytrobilokha.nibee.data.WebLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@WebFilter(urlPatterns = "/*")
public class LoggingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String UUID_PARAM_NAME = "uuid";
    private static final String USER_AGENT = "User-Agent";
    private static final String REFERER = "Referer";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("LoggingFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        String sessionUuid = (String) session.getAttribute(UUID_PARAM_NAME);
        if (sessionUuid == null) {
            sessionUuid = getOrCreateCookieUuid(req, resp);
            session.setAttribute(UUID_PARAM_NAME, sessionUuid);
        }
        WebLogEntry logEntry = WebLogEntry.getBuilder()
                .sessionId(session.getId())
                .uuid(sessionUuid)
                .requestUri(req.getRequestURI())
                .queryString(req.getQueryString())
                .serverPort(req.getServerPort())
                .clientIp(req.getRemoteAddr())
                .clientPort(req.getRemotePort())
                .userAgent(req.getHeader(USER_AGENT))
                .acceptEncoding(req.getHeader(ACCEPT_ENCODING))
                .referer(req.getHeader(REFERER))
                .build();
        LOGGER.info("Got request {}", logEntry);
        chain.doFilter(request, response);
    }

    private String getOrCreateCookieUuid(HttpServletRequest req, HttpServletResponse resp) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            Optional<Cookie> cookieOptional = Arrays.stream(cookies)
                    .filter(cookie -> UUID_PARAM_NAME.equals(cookie.getName()))
                    .findFirst();
            if (cookieOptional.isPresent()) {
                return cookieOptional.get().getValue();
            }
        }
        String uuid = UUID.randomUUID().toString();
        Cookie uuidCookie = new Cookie(UUID_PARAM_NAME, uuid);
        uuidCookie.setMaxAge(100_000_000);
        resp.addCookie(uuidCookie);
        return uuid;
    }

    @Override
    public void destroy() {
        LOGGER.info("LoggingFilter destroyed");
    }

}
