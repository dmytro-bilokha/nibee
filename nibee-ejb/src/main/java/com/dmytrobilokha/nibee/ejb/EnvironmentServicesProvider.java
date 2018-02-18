package com.dmytrobilokha.nibee.ejb;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@ApplicationScoped
public class EnvironmentServicesProvider {

    @Produces
    @Dependent
    Context getNamingContext() {
        try {
            return new InitialContext();
        } catch (NamingException ex) {
            throw new IllegalStateException("Failed to instantiate InitialContext", ex);
        }
    }

}
