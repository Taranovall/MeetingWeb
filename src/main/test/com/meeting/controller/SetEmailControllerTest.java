package com.meeting.controller;

import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.service.UserService;
import com.meeting.service.ValidationService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Constant.EMAIL;
import static util.Constant.ERROR_ATTR_NAME;
import static util.Constant.LAST_PAGE_URI_ATTRIBUTE_NAME;
import static util.Constant.USER;
import static util.Util.createUser;

class SetEmailControllerTest {

    private static final String ERROR_MSG = "Invalid email";
    private SetEmailController setEmailController;
    @Mock
    UserService userService;
    @Mock
    ValidationService validationService;
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
        setEmailController = new SetEmailController();
        userService = mock(UserService.class);
        validationService = mock(ValidationService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        setEmailController = null;
        validationService = null;
        userService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldSetEmailForUser() throws DataBaseException, ServletException, IOException {
        User user = createUser();
        String email = "user329@gmail.com";
        String redirectTo = "/account/4";

        setEmailController.setUserService(userService);
        setEmailController.setValidationService(validationService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn(redirectTo);
        when(session.getAttribute(USER)).thenReturn(user);
        when(req.getParameter(EMAIL)).thenReturn(email);
        when(validationService.emailValidator(anyString(), eq(req))).thenReturn(true);

        setEmailController.doPost(req, resp);

        assertEquals(email, user.getEmail());
    }

    @Test
    void shouldDisplayPageWithError() throws DataBaseException, ServletException, IOException {
        setEmailController.setUserService(userService);
        setEmailController.setValidationService(validationService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(ERROR_ATTR_NAME)).thenReturn(ERROR_MSG);

        setEmailController.doPost(req, resp);

        verify(session, times(1)).removeAttribute(ERROR_ATTR_NAME);
    }
}