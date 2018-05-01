package com.dmytrobilokha.nibee.web.param;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

public class ParamParserTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("uu-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);

    private HttpServletRequest mockRequest;

    @Before
    public void initMocks() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter(anyString())).thenReturn("");
    }

    private void setRequestParam(String name, String value) {
        Mockito.when(mockRequest.getParameter(name)).thenReturn(value);
    }

    @Test
    public void checkReturnsEmptyLong1() throws InvalidParamException {
        Optional<Long> paramValue = ParamParser.parseLong(mockRequest, "longParam");
        assertFalse(paramValue.isPresent());
    }

    @Test
    public void checkReturnsEmptyLong2() throws InvalidParamException {
        setRequestParam("longParam", null);
        Optional<Long> paramValue = ParamParser.parseLong(mockRequest, "longParam");
        assertFalse(paramValue.isPresent());
    }

    @Test(expected = InvalidParamException.class)
    public void checkThrowsOnInvalidLong() throws InvalidParamException {
        setRequestParam("longParam", "43A");
        Optional<Long> paramValue = ParamParser.parseLong(mockRequest, "longParam");
    }

    @Test
    public void checkParsesLong() throws InvalidParamException {
        setRequestParam("longParam", "43");
        Optional<Long> paramValue = ParamParser.parseLong(mockRequest, "longParam");
        assertTrue(paramValue.isPresent());
        assertEquals(Long.valueOf(43L), paramValue.get());
    }

    @Test
    public void checkReturnsEmptyDateTime1() throws InvalidParamException {
        Optional<LocalDateTime> paramValue = ParamParser.parseDateTime(mockRequest, "dateTime", DATE_TIME_FORMATTER);
        assertFalse(paramValue.isPresent());
    }

    @Test
    public void checkReturnsEmptyDateTime2() throws InvalidParamException {
        setRequestParam("dateTime", null);
        Optional<LocalDateTime> paramValue = ParamParser.parseDateTime(mockRequest, "dateTime", DATE_TIME_FORMATTER);
        assertFalse(paramValue.isPresent());
    }

    @Test(expected = InvalidParamException.class)
    public void checkThrowsOnInvalidDateTime() throws InvalidParamException {
        setRequestParam("dateTime", "2012-02-01TXX:23:00.000");
        Optional<LocalDateTime> paramValue = ParamParser.parseDateTime(mockRequest, "dateTime", DATE_TIME_FORMATTER);
    }

    @Test
    public void checkParsesDateTime() throws InvalidParamException {
        final String dateTimeString = "14-02-12T12:34:09.123";
        setRequestParam("dateTime", dateTimeString);
        Optional<LocalDateTime> paramValue = ParamParser.parseDateTime(mockRequest, "dateTime", DATE_TIME_FORMATTER);
        assertTrue(paramValue.isPresent());
        assertEquals(LocalDateTime.of(2014, 02, 12, 12, 34, 9, 123000000), paramValue.get());
    }

}