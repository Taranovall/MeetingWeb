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

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static util.Utils.createMeeting;
import static util.Utils.createUser;

class StopParticipatingControllerTest {

    private StopParticipatingController stopParticipatingController;
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
        stopParticipatingController = new StopParticipatingController();
        userService = mock(UserService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        stopParticipatingController = null;
        userService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void userShouldStopParticipateInMeeting() throws IOException, ServletException {
        String lastPageURI = "/meeting/2";

        stopParticipatingController.setUserService(userService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("lastPageURI")).thenReturn(lastPageURI);
        when(session.getAttribute("user")).thenReturn(createUser());
        when(session.getAttribute("meeting")).thenReturn(createMeeting());

        stopParticipatingController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(lastPageURI);
    }
}