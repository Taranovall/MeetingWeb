package com.meeting.controller.meeting;

import com.meeting.service.UserService;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Constant.LAST_PAGE_URI;
import static util.Constant.LAST_PAGE_URI_ATTRIBUTE_NAME;
import static util.Constant.MEETING;
import static util.Constant.USER;
import static util.Util.createMeeting;
import static util.Util.createUser;

class ParticipateControllerTest {

    private ParticipateController participateController;
    @Mock
    UserService userService;
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
        participateController = new ParticipateController();
        userService = mock(UserService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        participateController = null;
        userService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldMakeUserParticipantOfMeeting() throws IOException, ServletException {

        participateController.setUserService(userService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn(LAST_PAGE_URI);
        when(session.getAttribute(USER)).thenReturn(createUser());
        when(session.getAttribute(MEETING)).thenReturn(createMeeting());

        participateController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(LAST_PAGE_URI);
    }
}