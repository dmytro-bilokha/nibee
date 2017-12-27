package com.dmytrobilokha.nibee.ejb.config;

import com.dmytrobilokha.nibee.ejb.AppConstants;

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
import java.util.Properties;

@Singleton
@Startup
public class ConfigService {

    private static final String PROPERTIES_JNDI_NAME = AppConstants.APP_NAME + "/properties";

    private final EnumMap<ConfigProperty, String> propertyMap = new EnumMap(ConfigProperty.class);
    private Context namingContext;

    public ConfigService() {
        //Required by EJB spec
    }

    @Inject
    public ConfigService(Context namingContext) {
        this.namingContext = namingContext;
    }

    @PostConstruct
    void initConfigProperties() {
        readConfigProperties();
    }

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    void updateConfigProperties() {
        readConfigProperties();
    }

    private void readConfigProperties() {
        List<String> missingProperties = new ArrayList<>();
        Properties appProperties = getAppProperties();
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            String value = appProperties.getProperty(configProperty.getPropertyName());
            if (value == null)
                missingProperties.add(configProperty.getPropertyName());
            else
                propertyMap.put(configProperty, value);
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
    public String getAsString(ConfigProperty configProperty) {
        return propertyMap.get(configProperty);
    }

    @Lock(LockType.READ)
    public int getAsInt(ConfigProperty configProperty) {
        return Integer.parseInt(propertyMap.get(configProperty));
    }

}
