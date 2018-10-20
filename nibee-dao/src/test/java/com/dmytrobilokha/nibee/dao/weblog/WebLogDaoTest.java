package com.dmytrobilokha.nibee.dao.weblog;

import com.dmytrobilokha.nibee.dao.AbstractDaoTest;
import com.dmytrobilokha.nibee.data.WebLogRecord;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class WebLogDaoTest extends AbstractDaoTest {

    private WebLogDao webLogDao;

    @BeforeClass
    public void initFunctionAlias() {
        executeSqlStatement("CREATE ALIAS UUID_TO_BIN FOR \"com.dmytrobilokha.nibee.dao.H2dbMySqlFunctions.uuidToBin\"");
        executeSqlStatement("CREATE ALIAS INET6_ATON FOR \"com.dmytrobilokha.nibee.dao.H2dbMySqlFunctions.inet6AtoN\"");
    }

    @BeforeMethod
    public void initWebLogDao() {
        webLogDao = getMapper(WebLogDao.class);
    }

    public void insertsAndReturnsCount() {
        WebLogRecord logEntry = createWebLogEntry();
        int count = webLogDao.insertRecord(logEntry);
        assertEquals(1, count);
    }

    public void assignsIdOnInsert() {
        WebLogRecord logEntry = createWebLogEntry();
        assertNull(logEntry.getId());
        webLogDao.insertRecord(logEntry);
        assertNotNull(logEntry.getId());
    }

    public void incrementsCountOnInsert() {
        int total = webLogDao.countRecords();
        webLogDao.insertRecord(createWebLogEntry());
        int newTotal = webLogDao.countRecords();
        assertEquals(1, newTotal - total);
    }

    //H2DB doesn't support ORDER BY for DELETE operator
    @Test(groups = "broken")
    public void deletes() {
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