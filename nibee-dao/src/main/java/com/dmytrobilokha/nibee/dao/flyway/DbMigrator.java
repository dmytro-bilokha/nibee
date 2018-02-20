package com.dmytrobilokha.nibee.dao.flyway;

import org.flywaydb.core.Flyway;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.sql.DataSource;

@ApplicationScoped
public class DbMigrator {

    private final DataSource dataSource;

    @Inject
    public DbMigrator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void migrateDb() {
        if (dataSource == null) {
            throw new IllegalStateException("Unable to migrate DB, because injected dataSource is null");
        }
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations("classpath:db-script");
        flyway.setBaselineVersionAsString("0.0.0");
        flyway.setBaselineOnMigrate(true);
        flyway.migrate();
    }

    public void eagerInit(@Observes @Initialized(ApplicationScoped.class) Object initEvent) {
        //The methods does nothing. We need it just to ensure a CDI framework initializes the bean eagerly.
    }

}
