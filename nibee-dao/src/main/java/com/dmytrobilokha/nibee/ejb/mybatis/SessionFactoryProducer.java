package com.dmytrobilokha.nibee.ejb.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.cdi.SessionFactoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.IOException;
import java.io.Reader;

@ApplicationScoped
public class SessionFactoryProducer {

    private static final String MYBATIS_CONFIG_XML = "mybatis.xml";
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFactoryProducer.class);

    @ApplicationScoped
    @Produces
    @SessionFactoryProvider
    public SqlSessionFactory produce() throws Exception {
        LOGGER.info("produce() called");
        try(Reader reader = Resources.getResourceAsReader(MYBATIS_CONFIG_XML)) {
            return new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to produce myBatis SqlSessionFactory with config: "
                        + MYBATIS_CONFIG_XML);
        }
    }

}
