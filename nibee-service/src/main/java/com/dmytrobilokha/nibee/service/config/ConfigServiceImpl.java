package com.dmytrobilokha.nibee.service.config;

import com.dmytrobilokha.nibee.service.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Singleton
@Startup
public class ConfigServiceImpl implements ConfigService, ConfigServiceMXBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);
    private static final String PROPERTIES_JNDI_NAME = AppConstants.APP_NAME + "/properties";

    private final EnumMap<ConfigProperty, String> propertyMap = new EnumMap(ConfigProperty.class);
    private Context namingContext;

    public ConfigServiceImpl() {
        //Required by EJB spec
    }

    @Inject
    public ConfigServiceImpl(Context namingContext) {
        this.namingContext = namingContext;
    }

    @PostConstruct
    void initConfigProperties() {
        readConfigProperties();
        LOGGER.info("Successfully initialized the application properties");
    }

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    @Override
    public void updateConfigProperties() {
        readConfigProperties();
        LOGGER.info("Successfully updated the application properties");
    }

    @Lock(LockType.READ)
    @Override
    public Map<ConfigProperty, String> getProperties() {
        return Collections.unmodifiableMap(propertyMap);
    }

    private void readConfigProperties() {
        List<String> missingProperties = new ArrayList<>();
        Properties appProperties = getAppProperties();
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            String value = appProperties.getProperty(configProperty.getPropertyName());
            if (value == null) {
                missingProperties.add(configProperty.getPropertyName());
            } else {
                propertyMap.put(configProperty, value);
            }
        }
        if (!missingProperties.isEmpty()) {
            Collections.sort(missingProperties);
            throw new EJBException("Failed to load required config properties: " + missingProperties);
        }
    }

    private Properties getAppProperties() {
        try {
            return (Properties) namingContext.lookup(PROPERTIES_JNDI_NAME);
        } catch (NamingException | ClassCastException ex) {
            throw new EJBException("Failed to load the application properties bundle from the JNDI context", ex);
        }
    }

    @Lock(LockType.READ)
    @Override
    public String getAsString(ConfigProperty configProperty) {
        return propertyMap.get(configProperty);
    }

    @Lock(LockType.READ)
    @Override
    public int getAsInt(ConfigProperty configProperty) {
        return Integer.parseInt(propertyMap.get(configProperty));
    }

}
