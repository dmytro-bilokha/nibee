package com.dmytrobilokha.nibee.service.jmx;

import com.dmytrobilokha.nibee.service.AppConstants;
import com.dmytrobilokha.nibee.service.config.ConfigServiceMXBean;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Singleton
@Startup
public class JmxExposer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmxExposer.class);

    private List<Object> mxBeans;

    private MBeanServer platformMBeanServer;

    public JmxExposer() {
        //Required by EJB spec
    }

    @Inject
    public JmxExposer(MBeanServer mBeanServer
            , WebLogManagerMXBean webLogManagerMXBean, ConfigServiceMXBean configServiceMXBean) {
        this.platformMBeanServer = mBeanServer;
        List<Object> beans = new ArrayList<>();
        beans.add(webLogManagerMXBean);
        beans.add(configServiceMXBean);
        this.mxBeans = Collections.unmodifiableList(beans);
    }

    @PostConstruct
    public void registerJmx() {
        for (Object mxBean : mxBeans) {
            try {
                ObjectName objectName = getName(mxBean);
                platformMBeanServer.registerMBean(mxBean, objectName);
            } catch (InstanceAlreadyExistsException | MBeanRegistrationException
                    | NotCompliantMBeanException | MalformedObjectNameException | RuntimeMBeanException
                    | RuntimeErrorException | RuntimeOperationsException ex) {
                throw new IllegalStateException("Failed to register " + mxBean + " into JMX", ex);
            }
        }
        LOGGER.info("Registered into JMS beans: {}", mxBeans);
    }

    private ObjectName getName(Object mxBean) throws MalformedObjectNameException {
        return new ObjectName(AppConstants.APP_NAME + ":type="
                        + mxBean.getClass().getName());
    }

    @PreDestroy
    public void unregisterJmx() {
        for (Object mxBean : mxBeans) {
            try {
                ObjectName objectName = getName(mxBean);
                platformMBeanServer.unregisterMBean(objectName);
            } catch (InstanceNotFoundException | MBeanRegistrationException | RuntimeMBeanException | RuntimeErrorException
                    | RuntimeOperationsException | MalformedObjectNameException ex) {
                throw new IllegalStateException("Failed to unregister " + mxBean + " from JMX", ex);
            }
        }
        LOGGER.info("Unregistered from JMS beans: {}", mxBeans);
    }

}
