package com.meeting.controller;

import com.meeting.entitiy.User;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.ValidationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Constant.LAST_PAGE_URI_ATTRIBUTE_NAME;
import static util.Constant.USER;
import static util.Util.createUser;

class LoginControllerTest {

    private LoginController loginController;
    @Mock
    private ValidationService validationService;
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
        loginController = new LoginController();
        validationService = mock(ValidationService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        loginController = null;
        validationService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldLoginIntoAccountAndRedirectToPreviousPage() throws UserNotFoundException, ServletException, IOException {
        User user = createUser();
        when(req.getSession()).thenReturn(session);
        when(validationService.authValidator(req)).thenReturn(user);
        when(req.getSession().getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn("/");

        loginController.setValidationService(validationService);

        loginController.doPost(req, resp);

        verify(session, times(1)).setAttribute(USER, user);
    }

    @Test
    void shouldLoginIntoAccountAndRedirectToMainPage() throws UserNotFoundException, ServletException, IOException {
        User user = createUser();
        when(req.getSession()).thenReturn(session);
        when(validationService.authValidator(req)).thenReturn(user);
        when(req.getSession().getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn(null);

        loginController.setValidationService(validationService);

        loginController.doPost(req, resp);

        verify(session, times(1)).setAttribute(USER, user);
    }

    @Test
    void inputIncorrectDataTest() throws UserNotFoundException, ServletException, IOException {
        User user = createUser();
        Object errorMsg = "???????????????????????? ?? ???????????? ?????????????? ???? ????????????????????";
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);

        when(req.getSession()).thenReturn(session);
        when(validationService.authValidator(req)).thenReturn(user);
        when(req.getAttribute("message")).thenReturn(errorMsg);
        when(req.getSession().getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn(null);
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        doNothing().when(requestDispatcher).forward(req,resp);

        loginController.setValidationService(validationService);

        loginController.doPost(req, resp);

        verify(requestDispatcher, times(1)).forward(req, resp);
    }
}