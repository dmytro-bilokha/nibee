package com.dmytrobilokha.nibee.dao.weblog;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.WebLogEntry;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class WebLogDaoIT extends AbstractDaoTest {

    private WebLogDao webLogDao;

    @BeforeClass
    public static void initFunctionAlias() {
        executeSqlStatement("CREATE ALIAS UUID_TO_BIN FOR \"com.dmytrobilokha.nibee.dao.H2dbMySqlFunctions.uuidToBin\"");
        executeSqlStatement("CREATE ALIAS INET6_ATON FOR \"com.dmytrobilokha.nibee.dao.H2dbMySqlFunctions.inet6AtoN\"");
    }

    @Before
    public void initWebLogDao() {
        webLogDao = getMapper(WebLogDao.class);
    }

    @Test
    public void checkInsertsAndCounts() {
        WebLogEntry logEntry = createWebLogEntry();
        int count = webLogDao.insertEntry(logEntry);
        assertEquals(1, count);
    }

    @Test
    public void checkAssignsIdOnInsert() {
        WebLogEntry logEntry = createWebLogEntry();
        assertNull(logEntry.getId());
        webLogDao.insertEntry(logEntry);
        assertNotNull(logEntry.getId());
    }

    private WebLogEntry createWebLogEntry() {
        return WebLogEntry.getBuilder()
                .sessionId("session-id")
                .uuid("123e4567-e89b-12d3-a456-426655440000")
                .requestUri("/blog/")
                .queryString("ololo")
                .serverPort(8080)
                .clientIp("247.0.0.1")
                .clientPort(24001)
                .userAgent("Some user agent")
                .acceptEncoding("gzip,bzip2")
                .referer("https://google.com/")
                .build();
    }
}