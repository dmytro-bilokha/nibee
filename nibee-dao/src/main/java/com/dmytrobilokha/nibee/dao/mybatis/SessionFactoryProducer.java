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
import java.util.Arrays;

@ApplicationScoped
public class SessionFactoryProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFactoryProducer.class);
    private static final String[] MAPPER_PACKAGES = new String[]{
            "com.dmytrobilokha.nibee.dao.post"
            , "com.dmytrobilokha.nibee.dao.tag"
            , "com.dmytrobilokha.nibee.dao.weblog"
    };

    private final DataSource dataSource;

    @Inject
    public SessionFactoryProducer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @ApplicationScoped
    @Produces
    @SessionFactoryProvider
    public SqlSessionFactory produce() {
        LOGGER.info("Producing MyBatis SqlSessionFactory with mapper packages {}", Arrays.toString(MAPPER_PACKAGES));
        TransactionFactory transactionFactory = new ManagedTransactionFactory();
        Environment environment = new Environment("main", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        for (String mapperPackage : MAPPER_PACKAGES) {
            configuration.addMappers(mapperPackage);
        }
        return new SqlSessionFactoryBuilder().build(configuration);
    }

}
