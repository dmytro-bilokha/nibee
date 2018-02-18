package com.dmytrobilokha.nibee.dao.mybatis;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.mybatis.cdi.SessionFactoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.sql.DataSource;

@ApplicationScoped
public class SessionFactoryProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFactoryProducer.class);

    private final DataSource dataSource;

    @Inject
    public SessionFactoryProducer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @ApplicationScoped
    @Produces
    @SessionFactoryProvider
    public SqlSessionFactory produce() {
        LOGGER.info("Producing MyBatis SqlSessionFactory");
        TransactionFactory transactionFactory = new ManagedTransactionFactory();
        Environment environment = new Environment("main", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMappers("com.dmytrobilokha.nibee.dao.post");
        return new SqlSessionFactoryBuilder().build(configuration);
    }

}
