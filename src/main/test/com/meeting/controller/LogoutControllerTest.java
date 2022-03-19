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

import static org.mockito.Mockito.*;
import static util.Utils.createUser;

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
        when(session.getAttribute("lastPageURI")).thenReturn("/meeting/3");
        when(session.getAttribute("user")).thenReturn(createUser());
        logoutController.doGet(req, resp);
        verify(session,times(1)).invalidate();
        verify(resp, times(1)).sendRedirect("/meeting/3");
    }

    @Test
    void shouldLogoutAndRedirectToMainPage() throws IOException {
        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("lastPageURI")).thenReturn(null);
        when(session.getAttribute("user")).thenReturn(createUser());
        logoutController.doGet(req, resp);
        verify(session,times(1)).invalidate();
        verify(resp, times(1)).sendRedirect("/");
    }
}