package com.dmytrobilokha.nibee.service.jmx;

import com.dmytrobilokha.nibee.service.AppConstants;
import com.dmytrobilokha.nibee.service.weblog.WebLogManagerMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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

@Singleton
@Startup
public class JmxEjbService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmxEjbService.class);

    private MBeanServer platformMBeanServer;
    private WebLogManagerMXBean webLogManagerMXBean;

    public JmxEjbService() {
        //Required by EJB spec
    }

    @Inject
    public JmxEjbService(MBeanServer mBeanServer, WebLogManagerMXBean webLogManagerMXBean) {
        this.platformMBeanServer = mBeanServer;
        this.webLogManagerMXBean = webLogManagerMXBean;
    }

    @PostConstruct
    public void registerJmx() {
        try {
            ObjectName objectName = new ObjectName(AppConstants.APP_NAME + ":type="
                    + webLogManagerMXBean.getClass().getName());
            platformMBeanServer.registerMBean(webLogManagerMXBean, objectName);
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException
                | NotCompliantMBeanException | MalformedObjectNameException | RuntimeMBeanException
                | RuntimeErrorException | RuntimeOperationsException ex) {
            throw new IllegalStateException("Failed to register " + webLogManagerMXBean + " into JMX", ex);
        }
    }

    @PreDestroy
    public void unregisterJmx() {
        try {
            ObjectName objectName = new ObjectName(AppConstants.APP_NAME + ":type="
                    + webLogManagerMXBean.getClass().getName());
            platformMBeanServer.unregisterMBean(objectName);
        } catch (InstanceNotFoundException | MBeanRegistrationException | RuntimeMBeanException | RuntimeErrorException
                | RuntimeOperationsException | MalformedObjectNameException ex) {
            throw new IllegalStateException("Failed to unregister " + webLogManagerMXBean + " from JMX", ex);
        }
    }

}
