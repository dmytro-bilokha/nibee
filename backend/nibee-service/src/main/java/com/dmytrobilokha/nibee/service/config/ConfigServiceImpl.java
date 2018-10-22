package com.dmytrobilokha.nibee.service.config;

import com.dmytrobilokha.nibee.service.AppConstants;
import com.dmytrobilokha.nibee.service.EnvironmentServicesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Singleton
@Startup
public class ConfigServiceImpl implements ConfigService, ConfigServiceMXBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);
    private static final String PROPERTIES_JNDI_NAME = AppConstants.APP_NAME + "/properties";
    private static final String INIT_PROPERTIES = "build.properties";
    private static final List<ConfigProperty> JNDI_PROPERTIES = Collections.unmodifiableList(
            Arrays.stream(ConfigProperty.values())
                    .filter(prop -> !prop.isBuildDefined())
                    .collect(Collectors.toList()));
    private static final List<ConfigProperty> BUILD_PROPERTIES = Collections.unmodifiableList(
            Arrays.stream(ConfigProperty.values())
                    .filter(prop -> prop.isBuildDefined())
                    .collect(Collectors.toList()));

    private final EnumMap<ConfigProperty, String> propertyMap = new EnumMap(ConfigProperty.class);

    private Context namingContext;
    private EnvironmentServicesProvider environmentServicesProvider;

    public ConfigServiceImpl() {
        //Required by EJB spec
    }

    @Inject
    public ConfigServiceImpl(EnvironmentServicesProvider environmentServicesProvider) {
        this.namingContext = environmentServicesProvider.getNamingContext();
        this.environmentServicesProvider = environmentServicesProvider;
    }

    @PostConstruct
    void initConfigProperties() {
        readBuildProperties();
        readJndiProperties();
        validate();
        LOGGER.info("Successfully initialized the application properties");
    }

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    @Override
    public void updateConfigProperties() {
        readJndiProperties();
        LOGGER.info("Successfully updated the application properties");
    }

    @Lock(LockType.READ)
    @Override
    public Map<ConfigProperty, String> getProperties() {
        return Collections.unmodifiableMap(propertyMap);
    }

    private void readBuildProperties() {
        Properties buildProperties = getBuildProperties();
        readProperties(BUILD_PROPERTIES, buildProperties);
    }

    private Properties getBuildProperties() {
        Properties buildProperties = new Properties();
        try (BufferedInputStream bis = new BufferedInputStream(
                environmentServicesProvider.getResourceAsStream(INIT_PROPERTIES))) {
            buildProperties.load(bis);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read build properties from resource "
                    + INIT_PROPERTIES, ex);
        }
        return buildProperties;
    }

    private void readJndiProperties() {
        Properties appProperties = getJndiProperties();
        readProperties(JNDI_PROPERTIES, appProperties);
    }

    private void readProperties(List<ConfigProperty> configProperties, Properties rawProperties) {
        for (ConfigProperty configProperty : configProperties) {
            String value = rawProperties.getProperty(configProperty.getPropertyName());
            propertyMap.put(configProperty, value);
        }
    }

    private void validate() {
        List<String> missingProperties = new ArrayList<>();
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            if (propertyMap.get(configProperty) == null) {
                missingProperties.add(configProperty.getPropertyName());
            }
        }
        if (!missingProperties.isEmpty()) {
            Collections.sort(missingProperties);
            throw new IllegalStateException("Failed to load required config properties: " + missingProperties);
        }
    }

    private Properties getJndiProperties() {
        try {
            return (Properties) namingContext.lookup(PROPERTIES_JNDI_NAME);
        } catch (NamingException | ClassCastException ex) {
            throw new IllegalStateException("Failed to load the application properties bundle"
                    + PROPERTIES_JNDI_NAME + " from the JNDI context", ex);
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
