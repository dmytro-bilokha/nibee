package com.dmytrobilokha.nibee.service.config;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.NamingException;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class ConfigServiceTest {

    private static final String PROPERTIES_JNDI_NAME = "nibee/properties";

    private ConfigServiceImpl configService;
    private Context mockNamingContext;

    private Properties properties;

    @Before
    public void init() throws NamingException {
        this.properties = new Properties();
        this.mockNamingContext = Mockito.mock(Context.class);
        Mockito.when(mockNamingContext.lookup(PROPERTIES_JNDI_NAME)).thenReturn(properties);
        this.configService = new ConfigServiceImpl(mockNamingContext);
    }

    @Test
    public void checkReturnsConfigProperties() {
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            properties.put(configProperty.getPropertyName(), configProperty.name() + "-MOCK_VALUE");
        }
        configService.initConfigProperties();
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            assertEquals(configProperty.name() + "-MOCK_VALUE", configService.getAsString(configProperty));
        }
    }

    @Test(expected = EJBException.class)
    public void checkThrowsExceptionOnEmptyProperties() {
        configService.initConfigProperties();
    }

    @Test(expected = EJBException.class)
    public void checkThrowsEJBExceptionOnLookupFailure() throws NamingException {
        Mockito.reset(mockNamingContext);
        Mockito.when(mockNamingContext.lookup(PROPERTIES_JNDI_NAME)).thenThrow(new NamingException());
        configService.initConfigProperties();
    }

}
