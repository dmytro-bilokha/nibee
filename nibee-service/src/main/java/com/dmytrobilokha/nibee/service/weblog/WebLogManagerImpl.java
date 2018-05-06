package com.dmytrobilokha.nibee.service.weblog;

import com.dmytrobilokha.nibee.dao.weblog.WebLogDao;
import com.dmytrobilokha.nibee.service.AppConstants;
import com.dmytrobilokha.nibee.service.config.ConfigProperty;
import com.dmytrobilokha.nibee.service.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import java.lang.management.ManagementFactory;

@Singleton
@Startup
public class WebLogManagerImpl implements WebLogManagerMXBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebLogManagerImpl.class);

    private WebLogDao webLogDao;
    private ConfigService configService;
    private MBeanServer platformMBeanServer;
    private ObjectName objectName;

    public WebLogManagerImpl() {
        //Required by EJB spec
    }

    @Inject
    public WebLogManagerImpl(WebLogDao webLogDao, ConfigService configService) {
        this.webLogDao = webLogDao;
        this.configService = configService;
    }

    @PostConstruct
    void registerJmx() {
        platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            objectName = new ObjectName(AppConstants.APP_NAME + ":type=" + this.getClass().getName());
            platformMBeanServer.registerMBean(this, objectName); //TODO avoid leaking ejb's this
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException
                | MalformedObjectNameException | RuntimeMBeanException | RuntimeErrorException
                | RuntimeOperationsException ex) {
            throw new IllegalStateException("Failed to register into JMX", ex);
        }
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

    @PreDestroy
    void unregisterJmx() {
        try {
            platformMBeanServer.unregisterMBean(objectName);
        } catch (InstanceNotFoundException | MBeanRegistrationException | RuntimeMBeanException | RuntimeErrorException
                | RuntimeOperationsException ex) {
            throw new IllegalStateException("Failed to unregister from JMX", ex);
        }
    }

}
