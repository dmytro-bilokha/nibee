package com.dmytrobilokha.nibee.web.weblog;

import com.dmytrobilokha.nibee.data.WebLogRecord;
import com.dmytrobilokha.nibee.service.weblog.WebLogService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

@Test(groups = "web.unit")
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

    @BeforeMethod
    public void init() {
        initWebLogService();
        initSession();
        initRequest();
        responseMock = mock(HttpServletResponse.class);
        cookieHelperMock = mock(CookieHelper.class);
        loggingFilter = new LoggingFilter(webLogServiceMock, cookieHelperMock);
        filterChainMock = mock(FilterChain.class);
    }

    private void initSession() {
        sessionAttributes = new HashMap<>();
        sessionMock = mock(HttpSession.class);
        when(sessionMock.getId()).thenReturn("SESSION_ID");
        when(sessionMock.getAttribute(any(String.class)))
                .then(ans -> sessionAttributes.get(ans.getArgument(0)));
        doAnswer(ans -> {
            sessionAttributes.put(ans.getArgument(0), ans.getArgument(1));
            return null;
        }).when(sessionMock).setAttribute(any(String.class), any(Object.class));
    }

    private void initRequest() {
        requestHeaders = new HashMap<>();
        requestMock = mock(HttpServletRequest.class);
        when(requestMock.getSession()).thenReturn(sessionMock);
        when(requestMock.getRequestURI()).thenReturn("REQUEST_URI");
        when(requestMock.getQueryString()).thenReturn("QUERY_STRING");
        when(requestMock.getServerPort()).thenReturn(8080);
        when(requestMock.getRemoteAddr()).thenReturn("REMOTE_IP");
        when(requestMock.getRemotePort()).thenReturn(4242);
        when(requestMock.getHeader(any(String.class)))
                .then(ans -> requestHeaders.get(ans.getArgument(0)));
    }

    private void initWebLogService() {
        recordsInserted = new ArrayList<>();
        webLogServiceMock = mock(WebLogService.class);
        when(webLogServiceMock.insertRecord(any(WebLogRecord.class))).then(answer -> {
            recordsInserted.add(answer.getArgument(0));
            return 1;
        });
    }

    public void doesntBreakChain() throws IOException, ServletException {
        callDoFilter();
        verify(filterChainMock, times(1)).doFilter(requestMock, responseMock);
    }

    private void callDoFilter() {
        try {
            loggingFilter.doFilter(requestMock, responseMock, filterChainMock);
        } catch (IOException | ServletException ex) {
            fail("Got unexpected exception " + ex);
        }
    }

    public void createsSessionForHuman() {
        sessionAttributes.put("uuid", "007");
        requestHeaders.put("User-Agent", "Mozilla");
        callDoFilter();
        verify(requestMock, times(1)).getSession();
    }

    public void createsNoSessionForBot() {
        requestHeaders.put("User-Agent", "Googlebot");
        callDoFilter();
        verify(requestMock, times(0)).getSession();
    }

    public void createsNewCookie() {
        requestHeaders.put("User-Agent", "Mozilla");
        callDoFilter();
        verify(cookieHelperMock, times(1))
                .attachCookie(eq(responseMock), eq("uuid"), any(String.class), eq(1_000_000_000));
    }

    public void doesntCreateNewCookieWhenHaveOld() {
        requestHeaders.put("User-Agent", "Mozilla");
        when(cookieHelperMock.getCookieValue(eq(requestMock), eq("uuid"))).thenReturn("00-00-00");
        callDoFilter();
        verify(cookieHelperMock, times(0))
                .attachCookie(any(HttpServletResponse.class), any(String.class), any(String.class), anyInt());
    }

    public void savesDataToLog() {
        when(cookieHelperMock.getCookieValue(eq(requestMock), eq("uuid"))).thenReturn("00-00-01");
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

    public void firstGetsUuidFromSession() {
        sessionAttributes.put("uuid", "007");
        requestHeaders.put("User-Agent", "Mozilla");
        callDoFilter();
        verify(sessionMock, times(1)).getAttribute("uuid");
        verify(cookieHelperMock, times(0)).getCookieValue(any(), any());
    }

    public void copiesUuidFromCookieToSession() {
        when(cookieHelperMock.getCookieValue(eq(requestMock), eq("uuid"))).thenReturn("00-00-02");
        requestHeaders.put("User-Agent", "Mozilla");
        callDoFilter();
        assertEquals("00-00-02", sessionAttributes.get("uuid"));
    }

}