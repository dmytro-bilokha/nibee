package com.dmytrobilokha.nibee.ejb.config;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.NamingException;

import java.util.Arrays;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigServiceTest {

    private static final String PROPERTIES_JNDI_NAME = "nibee/properties";

    private ConfigService configService;
    private Context mockNamingContext;

    private Properties properties;

    @BeforeEach
    public void init() throws NamingException {
        this.properties = new Properties();
        this.mockNamingContext = Mockito.mock(Context.class);
        Mockito.when(mockNamingContext.lookup(PROPERTIES_JNDI_NAME)).thenReturn(properties);
        this.configService = new ConfigService(mockNamingContext);
    }

    @Test
    public void checkReturnsConfigProperties() {
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            properties.put(configProperty.getPropertyName(), configProperty.name() + "-MOCK_VALUE");
        }
        configService.initConfigProperties();
        assertAll(
                Arrays.stream(ConfigProperty.values())
                .map(cp ->
                        (() -> assertEquals(cp.name() + "-MOCK_VALUE", configService.getAsString(cp))))
        );
    }

    @Test
    public void checkThrowsExceptionOnEmptyProperties() {
        assertThrows(EJBException.class, () -> configService.initConfigProperties());
    }

    @Test
    public void checkThrowsEJBExceptionOnLookupFailure() throws NamingException {
        Mockito.reset(mockNamingContext);
        Mockito.when(mockNamingContext.lookup(PROPERTIES_JNDI_NAME)).thenThrow(new NamingException());
        assertThrows(EJBException.class, () -> configService.initConfigProperties());
    }

}
