package com.dmytrobilokha.nibee.web.initialization;

import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import com.dmytrobilokha.nibee.service.initialization.InitializationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Initializator implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Initializator.class);

    private InitializationService initializationService;
    private ConfigService configService;

    @Inject
    public Initializator(InitializationService initializationService, ConfigService configService) {
        this.initializationService = initializationService;
        this.configService = configService;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initializationService.onAppStart();
        ServletContext context = sce.getServletContext();
        context.setAttribute("appVersion", configService.getAsString(ConfigProperty.APP_VERSION));
        context.setAttribute("buildTimestamp", configService.getAsString(ConfigProperty.BUILD_TIMESTAMP));
        LOGGER.info("Web application initialization completed");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Web application destroyed");
    }

}
