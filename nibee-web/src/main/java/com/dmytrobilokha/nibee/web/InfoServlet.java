package com.dmytrobilokha.nibee.web;

import com.dmytrobilokha.nibee.ejb.config.ConfigProperty;
import com.dmytrobilokha.nibee.ejb.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/")
public class InfoServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfoServlet.class);

    @Inject
    private ConfigService configService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("InfoServlet doGet called");
        req.setAttribute("propertyName", ConfigProperty.POSTS_ROOT.getPropertyName());
        req.setAttribute("propertyEnum", ConfigProperty.POSTS_ROOT.name());
        req.setAttribute("propertyValue", configService.getAsString(ConfigProperty.POSTS_ROOT));
        NavigablePage.INFO.forwardTo(req, resp);
    }

}
