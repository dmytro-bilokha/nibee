package com.dmytrobilokha.nibee.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.management.MBeanServer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;

@ApplicationScoped
public class EnvironmentServicesProvider {

    //TODO why this method is public?
    @Produces
    @Dependent
    public Context getNamingContext() {
        try {
            return new InitialContext();
        } catch (NamingException ex) {
            throw new IllegalStateException("Failed to instantiate InitialContext", ex);
        }
    }

    //TODO why this method is public?
    @Produces
    @Dependent
    public MBeanServer getMBeanServer() {
        return  ManagementFactory.getPlatformMBeanServer();
    }

    //TODO why this method is public?
    @Produces
    @ApplicationScoped
    public Jsonb getJsonb() {
        return JsonbBuilder.create();
    }

    public InputStream getResourceAsStream(String resource) {
        return this.getClass().getClassLoader().getResourceAsStream(resource);
    }

}
