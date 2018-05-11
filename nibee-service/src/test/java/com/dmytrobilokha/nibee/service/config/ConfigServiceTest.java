package com.dmytrobilokha.nibee.service.config;


import com.dmytrobilokha.nibee.service.EnvironmentServicesProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.naming.Context;
import javax.naming.NamingException;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

public class ConfigServiceTest {

    private static final String PROPERTIES_JNDI_NAME = "nibee/properties";
    private static final String BUILD_PROPERTIES = "applicationVersion=APP_VERSION-MOCK_VALUE\r"
                        + "buildTimestamp=BUILD_TIMESTAMP-MOCK_VALUE\r";

    private ConfigServiceImpl configService;
    private Context mockNamingContext;
    private EnvironmentServicesProvider mockProvider;

    private Properties properties;

    @Before
    public void init() throws NamingException {
        this.properties = new Properties();
        this.mockNamingContext = Mockito.mock(Context.class);
        Mockito.when(mockNamingContext.lookup(PROPERTIES_JNDI_NAME)).thenReturn(properties);
        this.mockProvider = Mockito.mock(EnvironmentServicesProvider.class);
        Mockito.when(mockProvider.getNamingContext()).thenReturn(mockNamingContext);
        Mockito.when(mockProvider.getResourceAsStream(anyString()))
                .thenReturn(new ByteArrayInputStream(BUILD_PROPERTIES.getBytes()));
        this.configService = new ConfigServiceImpl(mockProvider);
    }

    @Test
    public void checkReturnsConfigProperties() {
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            properties.put(configProperty.getPropertyName(), configProperty.name() + "-MOCK_VALUE");
        }
        configService.initConfigProperties();
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            if (configProperty.isBuildDefined()) {
                assertNotNull(configService.getAsString(configProperty));
            } else {
                assertEquals(configProperty.name() + "-MOCK_VALUE", configService.getAsString(configProperty));
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void checkThrowsExceptionOnEmptyProperties() {
        configService.initConfigProperties();
    }

    @Test(expected = IllegalStateException.class)
    public void checkThrowsExceptionOnLookupFailure() throws NamingException {
        Mockito.when(mockNamingContext.lookup(PROPERTIES_JNDI_NAME)).thenThrow(new NamingException());
        configService.initConfigProperties();
    }

}
