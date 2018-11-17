package com.dmytrobilokha.nibee.service.config;

import com.dmytrobilokha.nibee.service.EnvironmentServicesProvider;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.naming.Context;
import javax.naming.NamingException;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test(groups = "service.unit")
public class ConfigServiceTest {

    private static final String PROPERTIES_JNDI_NAME = "nibee/properties";
    private static final String BUILD_PROPERTIES = "applicationVersion=APP_VERSION-MOCK_VALUE\r"
                        + "buildTimestamp=BUILD_TIMESTAMP-MOCK_VALUE\r";

    private ConfigServiceImpl configService;
    private Context mockNamingContext;
    private EnvironmentServicesProvider mockProvider;

    private Properties properties;

    @BeforeClass
    public void init() {
        properties = new Properties();
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            properties.put(configProperty.getPropertyName(), configProperty.name() + "-MOCK_VALUE");
        }
        mockNamingContext = Mockito.mock(Context.class);
        mockProvider = Mockito.mock(EnvironmentServicesProvider.class);
    }

    @BeforeMethod
    public void setupMockDefaults() throws NamingException {
        when(mockNamingContext.lookup(PROPERTIES_JNDI_NAME)).thenReturn(properties);
        when(mockProvider.getResourceAsStream(anyString()))
                .thenReturn(new ByteArrayInputStream(BUILD_PROPERTIES.getBytes()));
        configService = new ConfigServiceImpl(mockProvider, mockNamingContext);
    }

    @AfterMethod
    public void resetMocks() {
        reset(mockNamingContext, mockProvider);
    }

    public void returnsConfigProperties() {
        configService.initConfigProperties();
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            if (configProperty.isBuildDefined()) {
                assertNotNull(configService.getAsString(configProperty));
            } else {
                assertEquals(configProperty.name() + "-MOCK_VALUE", configService.getAsString(configProperty));
            }
        }
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void throwsExceptionOnEmptyProperties() throws NamingException {
        when(mockNamingContext.lookup(PROPERTIES_JNDI_NAME)).thenReturn(new Properties());
        configService.initConfigProperties();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void throwsExceptionOnLookupFailure() throws NamingException {
        when(mockNamingContext.lookup(PROPERTIES_JNDI_NAME)).thenThrow(new NamingException());
        configService.initConfigProperties();
    }

}
