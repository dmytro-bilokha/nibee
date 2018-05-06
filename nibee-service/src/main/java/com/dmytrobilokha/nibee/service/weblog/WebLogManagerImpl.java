package com.dmytrobilokha.nibee.service.weblog;

import com.dmytrobilokha.nibee.dao.weblog.WebLogDao;
import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class WebLogManagerImpl implements WebLogManagerMXBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebLogManagerImpl.class);

    private WebLogDao webLogDao;
    private ConfigService configService;

    public WebLogManagerImpl() {
        //Required by EJB spec
    }

    @Inject
    public WebLogManagerImpl(WebLogDao webLogDao, ConfigService configService) {
        this.webLogDao = webLogDao;
        this.configService = configService;
    }

    @Schedule(minute = "*/30", hour = "*", persistent = false)
    @Override
    public void truncateWebLog() {
        int keepLogRecord = configService.getAsInt(ConfigProperty.WEB_LOG_RECORDS_MAX);
        int recordsCount = webLogDao.countRecords();
        if (recordsCount <= keepLogRecord) {
            LOGGER.info("Total record in the web log {}, max records {}. No truncate required."
                    , recordsCount, keepLogRecord);
            return;
        }
        int toDelete = recordsCount - keepLogRecord;
        int deleted = webLogDao.deleteOldestRecords(toDelete);
        LOGGER.info("Total record in the web log {}, max records {}, to delete {}, actually deleted {}."
                , recordsCount, keepLogRecord, toDelete, deleted);
    }

}
