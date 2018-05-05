package com.dmytrobilokha.nibee.service.initialization;

import com.dmytrobilokha.nibee.dao.flyway.DbMigrator;

import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class InitializationServiceImpl implements InitializationService {

    private DbMigrator dbMigrator;

    public InitializationServiceImpl() {
        //Constructor required by the EJB spec
    }

    @Inject
    public InitializationServiceImpl(DbMigrator dbMigrator) {
        this.dbMigrator = dbMigrator;
    }

    @Override
    public void onAppStart() {
        dbMigrator.migrateDb();
    }

}
