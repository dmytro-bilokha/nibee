package com.dmytrobilokha.nibee.dao.weblog;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.WebLogRecord;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
    public void checkInsertsAndReturnsCount() {
        WebLogRecord logEntry = createWebLogEntry();
        int count = webLogDao.insertRecord(logEntry);
        assertEquals(1, count);
    }

    @Test
    public void checkAssignsIdOnInsert() {
        WebLogRecord logEntry = createWebLogEntry();
        assertNull(logEntry.getId());
        webLogDao.insertRecord(logEntry);
        assertNotNull(logEntry.getId());
    }

    @Test
    public void checkIncrementsCountOnInsert() {
        int total = webLogDao.countRecords();
        webLogDao.insertRecord(createWebLogEntry());
        int newTotal = webLogDao.countRecords();
        assertEquals(1, newTotal - total);
    }

    @Ignore("H2DB doesn't support ORDER BY for DELETE operator")
    @Test
    public void checkDeletes() {
        webLogDao.insertRecord(createWebLogEntry());
        webLogDao.insertRecord(createWebLogEntry());
        webLogDao.insertRecord(createWebLogEntry());
        int total = webLogDao.countRecords();
        int deleteCount = webLogDao.deleteOldestRecords(2);
        assertEquals(2, deleteCount);
        int newTotal = webLogDao.countRecords();
        assertEquals(2, total - newTotal);
    }

    private WebLogRecord createWebLogEntry() {
        return WebLogRecord.getBuilder()
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