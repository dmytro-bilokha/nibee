package com.dmytrobilokha.nibee.dao.flyway;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;

@ApplicationScoped
public class DbMigrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbMigrator.class);

    private DataSource dataSource;

    public DbMigrator() {
        //Constructor required to make DbMigrator proxable
    }

    @Inject
    public DbMigrator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void migrateDb() {
        if (dataSource == null) {
            throw new IllegalStateException("Unable to migrate DB, because injected dataSource is null");
        }
        LOGGER.info("Initializing flyway DB migration");
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations("classpath:db-script");
        flyway.setBaselineVersionAsString("0.0.0");
        flyway.setBaselineOnMigrate(true);
        flyway.migrate();
    }

}
