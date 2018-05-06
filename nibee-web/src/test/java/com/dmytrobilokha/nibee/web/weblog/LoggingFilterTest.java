package com.dmytrobilokha.nibee.web.weblog;

import com.dmytrobilokha.nibee.data.WebLogRecord;
import com.dmytrobilokha.nibee.service.weblog.WebLogService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

public class LoggingFilterTest {

    private LoggingFilter loggingFilter;
    private WebLogService webLogServiceMock;
    private List<WebLogRecord> recordsInserted;
    private FilterChain filterChainMock;
    private HttpServletResponse responseMock;
    private HttpServletRequest requestMock;
    private Map<String, String> requestHeaders;
    private HttpSession sessionMock;
    private Map<String, Object> sessionAttributes;
    private CookieHelper cookieHelperMock;

    @Before
    public void init() {
        initWebLogService();
        initSession();
        initRequest();
        responseMock = Mockito.mock(HttpServletResponse.class);
        cookieHelperMock = Mockito.mock(CookieHelper.class);
        loggingFilter = new LoggingFilter(webLogServiceMock, cookieHelperMock);
        filterChainMock = Mockito.mock(FilterChain.class);
    }

    private void initSession() {
        sessionAttributes = new HashMap<>();
        sessionMock = Mockito.mock(HttpSession.class);
        Mockito.when(sessionMock.getId()).thenReturn("SESSION_ID");
        Mockito.when(sessionMock.getAttribute(any(String.class)))
                .then(ans -> sessionAttributes.get(ans.getArgument(0)));
        Mockito.doAnswer(ans -> {
            sessionAttributes.put(ans.getArgument(0), ans.getArgument(1));
            return null;
        }).when(sessionMock).setAttribute(any(String.class), any(Object.class));
    }

    private void initRequest() {
        requestHeaders = new HashMap<>();
        requestMock = Mockito.mock(HttpServletRequest.class);
        Mockito.when(requestMock.getSession()).thenReturn(sessionMock);
        Mockito.when(requestMock.getRequestURI()).thenReturn("REQUEST_URI");
        Mockito.when(requestMock.getQueryString()).thenReturn("QUERY_STRING");
        Mockito.when(requestMock.getServerPort()).thenReturn(8080);
        Mockito.when(requestMock.getRemoteAddr()).thenReturn("REMOTE_IP");
        Mockito.when(requestMock.getRemotePort()).thenReturn(4242);
        Mockito.when(requestMock.getHeader(any(String.class)))
                .then(ans -> requestHeaders.get(ans.getArgument(0)));
    }

    private void initWebLogService() {
        recordsInserted = new ArrayList<>();
        webLogServiceMock = Mockito.mock(WebLogService.class);
        Mockito.when(webLogServiceMock.insertRecord(any(WebLogRecord.class))).then(answer -> {
            recordsInserted.add(answer.getArgument(0));
            return 1;
        });
    }

    @Test
    public void checkDoesntBreakChain() throws IOException, ServletException {
        callDoFilter();
        Mockito.verify(filterChainMock, times(1)).doFilter(requestMock, responseMock);
    }

    private void callDoFilter() {
        try {
            loggingFilter.doFilter(requestMock, responseMock, filterChainMock);
        } catch (IOException | ServletException ex) {
            fail("Got unexpected exception " + ex);
        }
    }

    @Test
    public void checkCreatesSessionForHuman() {
        sessionAttributes.put("uuid", "007");
        requestHeaders.put("User-Agent", "Mozilla");
        callDoFilter();
        Mockito.verify(requestMock, times(1)).getSession();
    }

    @Test
    public void checkCreatesNoSessionForBot() {
        requestHeaders.put("User-Agent", "Googlebot");
        callDoFilter();
        Mockito.verify(requestMock, times(0)).getSession();
    }

    @Test
    public void checkCreatesNewCookie() {
        requestHeaders.put("User-Agent", "Mozilla");
        callDoFilter();
        Mockito.verify(cookieHelperMock, times(1))
                .attachCookie(eq(responseMock), eq("uuid"), any(String.class), eq(1_000_000_000));
    }

    @Test
    public void checkDoesntCreateNewCookieWhenHaveOld() {
        requestHeaders.put("User-Agent", "Mozilla");
        Mockito.when(cookieHelperMock.getCookieValue(eq(requestMock), eq("uuid"))).thenReturn("00-00-00");
        callDoFilter();
        Mockito.verify(cookieHelperMock, times(0))
                .attachCookie(any(HttpServletResponse.class), any(String.class), any(String.class), anyInt());
    }

    @Test
    public void checkSavesDataToLog() {
        Mockito.when(cookieHelperMock.getCookieValue(eq(requestMock), eq("uuid"))).thenReturn("00-00-01");
        requestHeaders.put("User-Agent", "Mozilla");
        requestHeaders.put("Referer", "REFERER");
        requestHeaders.put("Accept-Encoding", "ENCODING");
        callDoFilter();
        assertEquals(1, recordsInserted.size());
        WebLogRecord record = recordsInserted.get(0);
        assertEquals(8080, record.getServerPort());
        assertEquals(4242, record.getClientPort());
        assertEquals("Mozilla", record.getUserAgent());
        assertEquals("REFERER", record.getReferer());
        assertEquals("ENCODING", record.getAcceptEncoding());
        assertEquals("QUERY_STRING", record.getQueryString());
        assertEquals("00-00-01", record.getUuid());
        assertEquals("REMOTE_IP", record.getClientIp());
        assertEquals("SESSION_ID", record.getSessionId());
        assertNull(record.getCreatedOn());
        assertNull(record.getId());
    }

    @Test
    public void checkFirstGetsUuidFromSession() {
        sessionAttributes.put("uuid", "007");
        requestHeaders.put("User-Agent", "Mozilla");
        callDoFilter();
        Mockito.verify(sessionMock, times(1)).getAttribute("uuid");
        Mockito.verify(cookieHelperMock, times(0)).getCookieValue(any(), any());
    }

    @Test
    public void checkCopiesUuidFromCookieToSession() {
        Mockito.when(cookieHelperMock.getCookieValue(eq(requestMock), eq("uuid"))).thenReturn("00-00-02");
        requestHeaders.put("User-Agent", "Mozilla");
        callDoFilter();
        assertEquals("00-00-02", sessionAttributes.get("uuid"));
    }

}