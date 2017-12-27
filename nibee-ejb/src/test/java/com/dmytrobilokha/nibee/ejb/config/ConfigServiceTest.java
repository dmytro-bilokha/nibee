package com.dmytrobilokha.nibee.ejb.config;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.naming.Context;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigServiceTest {

    private ConfigService configService;
    private Context mockNamingContext;

    @BeforeEach
    public void init() {
        this.configService = new ConfigService();
        this.mockNamingContext = Mockito.mock(Context.class);

    }

    @Test
    public void checkItAlwaysWorks() {
        assertEquals(2, 1 + 1);
    }

}
