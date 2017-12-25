package com.dmytrobilokha.nibee.ejb;

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
import java.util.EnumMap;

@Singleton
@Startup
public class ConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigService.class);

    private final EnumMap<ConfigProperty, String> propertyMap = new EnumMap(ConfigProperty.class);

    @Inject
    private Context namingContext;

    @PostConstruct
    void initConfigProperties() {
        readConfigProperties();
    }

    @Schedule(minute = "*/1", hour = "*", persistent = false)
    void updateConfigProperties() {
        readConfigProperties();
    }

    private void readConfigProperties() {
        boolean missingProperty = false;
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            try {
                propertyMap.put(configProperty, (String) namingContext.lookup(configProperty.getPropertyName()));
            } catch (NamingException ex) {
                missingProperty = true;
                LOGGER.error("Failed to read value for property {}", configProperty.getPropertyName(), ex);
            }
        }
        if (missingProperty)
            throw new EJBException("Failed to load all required config property values." +
                    " Check log to see list of missing properties.");
    }

    @Lock(LockType.READ)
    public String getAsString(ConfigProperty configProperty) {
        return propertyMap.get(configProperty);
    }

}
