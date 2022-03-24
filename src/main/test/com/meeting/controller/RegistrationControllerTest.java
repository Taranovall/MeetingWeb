package com.meeting.controller;

import com.meeting.service.UserService;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Util.createUser;

class RegistrationControllerTest {

    private RegistrationController registrationController;

    @Mock
    UserService userService;
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
        registrationController = new RegistrationController();
        userService = mock(UserService.class);
        validationService = mock(ValidationService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        registrationController = null;
        userService = null;
        validationService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldRegisterAccount() throws IOException, ServletException {
        registrationController.setUserService(userService);
        registrationController.setValidationService(validationService);

        when(validationService.registrationValidator(req)).thenReturn(createUser());
        when(req.getSession()).thenReturn(session);

        registrationController.doPost(req,resp);

        verify(resp,times(1)).sendRedirect("/");
    }

    @Test
    void shouldReloadPageBecauseOfInvalidData() throws ServletException, IOException {
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);

        registrationController.setValidationService(validationService);

        when(validationService.registrationValidator(req)).thenReturn(null);
        when(req.getSession()).thenReturn(session);
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        registrationController.doPost(req,resp);

        verify(requestDispatcher,times(1)).forward(req, resp);
    }
}