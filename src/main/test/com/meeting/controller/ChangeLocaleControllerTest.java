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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Constant.LANGUAGE_ATTR_NAME;
import static util.Constant.LAST_PAGE_URI_ATTRIBUTE_NAME;

class ChangeLocaleControllerTest {

    private static final String LOCALE = "locale";
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
        when(req.getParameter(LOCALE)).thenReturn("uk");
        when(session.getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn(lastURI);

        changeLocaleController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(lastURI);
        verify(session, times(1)).setAttribute(LANGUAGE_ATTR_NAME, "uk");
    }
}