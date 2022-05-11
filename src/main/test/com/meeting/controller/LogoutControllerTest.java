package com.meeting.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Constant.LAST_PAGE_URI_ATTRIBUTE_NAME;
import static util.Constant.USER;
import static util.Util.createUser;

class LogoutControllerTest {

    private LogoutController logoutController;
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private HttpSession session;
    @Mock
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        logoutController = new LogoutController();
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        logoutController = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldLogoutAndRedirectToPreviousPage() throws IOException {
        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn("/meeting/3");
        when(session.getAttribute(USER)).thenReturn(createUser());
        logoutController.doGet(req, resp);
        verify(session, times(1)).invalidate();
        verify(resp, times(1)).sendRedirect("/meeting/3");
    }

    @Test
    void shouldLogoutAndRedirectToMainPage() throws IOException {
        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn(null);
        when(session.getAttribute(USER)).thenReturn(createUser());
        logoutController.doGet(req, resp);
        verify(session, times(1)).invalidate();
        verify(resp, times(1)).sendRedirect("/");
    }
}