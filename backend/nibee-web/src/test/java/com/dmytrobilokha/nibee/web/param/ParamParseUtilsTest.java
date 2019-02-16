package com.dmytrobilokha.nibee.web.param;

import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@Test(groups = "web.unit")
public class ParamParseUtilsTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("uu-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);

    private HttpServletRequest mockRequest;

    @BeforeClass
    public void init() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
    }

    @BeforeMethod
    public void setupMockDefaults() {
        when(mockRequest.getParameter(anyString())).thenReturn("");
    }

    @AfterMethod
    public void resetMocks() {
        reset(mockRequest);
    }

    private void setRequestParam(String name, String value) {
        when(mockRequest.getParameter(name)).thenReturn(value);
    }

    public void returnsEmptyLongWhenNoParam() throws InvalidParamException {
        Long paramValue = ParamParseUtils.parseLong(mockRequest, "longParam");
        assertNull(paramValue);
    }

    public void returnsEmptyLongWhenParamIsNull() throws InvalidParamException {
        setRequestParam("longParam", null);
        Long paramValue = ParamParseUtils.parseLong(mockRequest, "longParam");
        assertNull(paramValue);
    }

    @Test(expectedExceptions = InvalidParamException.class)
    public void checkThrowsOnInvalidLong() throws InvalidParamException {
        setRequestParam("longParam", "43A");
        Long paramValue = ParamParseUtils.parseLong(mockRequest, "longParam");
    }

    public void parsesLong() throws InvalidParamException {
        setRequestParam("longParam", "43");
        Long paramValue = ParamParseUtils.parseLong(mockRequest, "longParam");
        assertNotNull(paramValue);
        assertEquals(Long.valueOf(43L), paramValue);
    }

    public void returnsEmptyDateTimeWhenNoParam() throws InvalidParamException {
        LocalDateTime paramValue = ParamParseUtils.parseDateTime(mockRequest, "dateTime", DATE_TIME_FORMATTER);
        assertNull(paramValue);
    }

    public void returnsEmptyDateTimeWhenParamIsNull() throws InvalidParamException {
        setRequestParam("dateTime", null);
        LocalDateTime paramValue = ParamParseUtils.parseDateTime(mockRequest, "dateTime", DATE_TIME_FORMATTER);
        assertNull(paramValue);
    }

    @Test(expectedExceptions = InvalidParamException.class)
    public void throwsOnInvalidDateTime() throws InvalidParamException {
        setRequestParam("dateTime", "2012-02-01TXX:23:00.000");
        LocalDateTime paramValue = ParamParseUtils.parseDateTime(mockRequest, "dateTime", DATE_TIME_FORMATTER);
    }

    public void parsesDateTime() throws InvalidParamException {
        final String dateTimeString = "14-02-12T12:34:09.123";
        setRequestParam("dateTime", dateTimeString);
        LocalDateTime paramValue = ParamParseUtils.parseDateTime(mockRequest, "dateTime", DATE_TIME_FORMATTER);
        assertNotNull(paramValue);
        assertEquals(LocalDateTime.of(2014, 02, 12, 12, 34, 9, 123000000), paramValue);
    }

}
