package com.meeting.controller;

import com.meeting.util.Constant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ErrorControllerTest {

    private static final String ERROR_ATTR_NAME = "javax.servlet.error.message";
    private static final String ERROR_MSG = "File '123K.docx' not found!";
    private ErrorController errorController;
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        errorController = new ErrorController();
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        errorController = null;
        req = null;
        resp = null;
    }

    @Test
    void shouldDisplayErrorPage() throws ServletException, IOException {
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        when(req.getAttribute(ERROR_ATTR_NAME)).thenReturn(ERROR_MSG);
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        errorController.doGet(req, resp);

        verify(req, times(1)).getRequestDispatcher(Constant.PATH_TO_ERROR_JSP);
        verify(requestDispatcher, times(1)).forward(req, resp);
    }
}