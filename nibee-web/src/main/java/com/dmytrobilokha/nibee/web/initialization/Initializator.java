package com.dmytrobilokha.nibee.web.initialization;

import com.dmytrobilokha.nibee.service.initialization.InitializationService;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Initializator implements ServletContextListener {

    private InitializationService initializationService;

    @Inject
    public Initializator(InitializationService initializationService) {
        this.initializationService = initializationService;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initializationService.onAppStart();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //Do nothing for now...
    }

}
