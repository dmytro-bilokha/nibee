package com.dmytrobilokha.nibee.dao.weblog;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import org.junit.Before;
import org.junit.Test;

public class WebLogDaoIT extends AbstractDaoTest {

    private WebLogDao webLogDao;

    @Before
    public void initWebLogDao() {
        webLogDao = getMapper(WebLogDao.class);
    }

    @Test
    public void checkDbMigration() {
        //TODO: remove the test and add some another more useful...
    }

}