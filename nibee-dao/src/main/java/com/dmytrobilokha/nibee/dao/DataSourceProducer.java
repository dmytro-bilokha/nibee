package com.dmytrobilokha.nibee.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@ApplicationScoped
public class DataSourceProducer {

    private static final String DATASOURCE_NAME = "jdbc/nibeeDataSource";

    @Dependent
    @Produces
    public DataSource produce() {
        try {
            InitialContext context = new InitialContext();
            DataSource dataSource = (DataSource) context.lookup(DATASOURCE_NAME);
            if (dataSource != null) {
                return dataSource;
            }
            throw new IllegalStateException("Failed to lookup datasource " + DATASOURCE_NAME
                    + ". Returned value is null");
        } catch (NamingException ex) {
            throw new IllegalStateException("Failed to lookup datasource " + DATASOURCE_NAME
                    + ". Naming exception has been thrown.", ex);
        }
    }

}
