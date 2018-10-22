package com.dmytrobilokha.nibee.web.weblog;

import com.dmytrobilokha.nibee.data.WebLogRecord;
import com.dmytrobilokha.nibee.service.weblog.WebLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

/** 
 * Here some javadocs for you
 */
@WebFilter(urlPatterns = "/*")
public class LoggingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String UUID_PARAM_NAME = "uuid";
    private static final String USER_AGENT = "User-Agent";
    private static final String REFERER = "Referer";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";

    private WebLogService webLogService;
    private CookieHelper cookieHelper;

    @Inject
    public LoggingFilter(WebLogService webLogService, CookieHelper cookieHelper) {
        this.webLogService = webLogService;
        this.cookieHelper = cookieHelper;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("LoggingFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String userAgent = req.getHeader(USER_AGENT);
        String sessionId = null;
        String sessionUuid = null;
        if (isUserAgentFromHuman(userAgent)) {
            HttpSession session = req.getSession();
            sessionId = session.getId();
            sessionUuid = (String) session.getAttribute(UUID_PARAM_NAME);
            if (sessionUuid == null) {
                sessionUuid = getOrCreateCookieUuid(req, resp);
                session.setAttribute(UUID_PARAM_NAME, sessionUuid);
            }
        }
        WebLogRecord logEntry = WebLogRecord.getBuilder()
                .sessionId(sessionId)
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
        webLogService.insertRecord(logEntry);
        chain.doFilter(request, response);
    }

    private boolean isUserAgentFromHuman(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return false;
        }
        String userAgentLowerCase = userAgent.toLowerCase();
        boolean isUserAgentFromBot =
                //Google web crawlers
                userAgentLowerCase.contains("googlebot")
                || userAgentLowerCase.contains("mediapartners-google")
                || userAgentLowerCase.contains("adsbot")
                || userAgentLowerCase.contains("developers.google")
                //MS Bing bot
                || userAgentLowerCase.contains("bingbot")
                //Yahoo Slurp bot
                || userAgentLowerCase.contains("slurp")
                //DuckDuck Go search bot
                || userAgentLowerCase.contains("duckduckbot")
                //Chinese Baidu search bot
                || userAgentLowerCase.contains("baiduspider")
                //Yandex search bot
                || userAgentLowerCase.contains("yandexbot")
                //Chinese Sogou search bot
                || userAgentLowerCase.contains("sogou")
                //French-based Exalead search bot
                || userAgentLowerCase.contains("exabot")
                //Facebook external hit
                || userAgentLowerCase.contains("facebot")
                || userAgentLowerCase.contains("facebookexternalhit")
                //Alexa crawler
                || userAgentLowerCase.contains("ia_archiver")
                ;
        return !isUserAgentFromBot;
    }

    private String getOrCreateCookieUuid(HttpServletRequest req, HttpServletResponse resp) {
        String uuid = cookieHelper.getCookieValue(req, UUID_PARAM_NAME);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            cookieHelper.attachCookie(resp, UUID_PARAM_NAME, uuid, 1_000_000_000);
        }
        return uuid;
    }

    @Override
    public void destroy() {
        LOGGER.info("LoggingFilter destroyed");
    }

}
