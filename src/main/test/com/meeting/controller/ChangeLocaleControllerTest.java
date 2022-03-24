package com.meeting.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.mockito.Mockito.*;

class ChangeLocaleControllerTest {

    private final ChangeLocaleController changeLocaleController = new ChangeLocaleController();
    @Mock
    private static HttpServletRequest req;
    @Mock
    private static HttpServletResponse resp;
    @Mock
    private static HttpSession session;
    @Mock
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void shouldChangeLanguageFromUkToEn() throws IOException, ServletException {
        String lastURI = "/meeting/4";
        when(req.getSession()).thenReturn(session);
        when(req.getParameter("locale")).thenReturn("uk");
        when(session.getAttribute("lastPageURI")).thenReturn(lastURI);

        changeLocaleController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(lastURI);
        verify(session, times(1)).setAttribute("language", "uk");
    }
}