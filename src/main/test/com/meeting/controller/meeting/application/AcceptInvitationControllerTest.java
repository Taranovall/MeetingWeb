package com.meeting.controller.meeting.application;

import com.meeting.entitiy.User;
import com.meeting.service.SpeakerService;
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
import static util.Utils.createUserWithRoleModerator;

class AcceptInvitationControllerTest {

    private AcceptInvitationController acceptInvitationController;
    @Mock
    SpeakerService speakerService;
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
        acceptInvitationController = new AcceptInvitationController();
        speakerService = mock(SpeakerService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        acceptInvitationController = null;
        session = null;
        req = null;
        resp = null;
    }

    @Test
    void shouldAcceptApplicationSentBySpeaker() throws IOException, ServletException {
        User userWithRoleModerator = createUserWithRoleModerator();
        String lastPageURI = "/meeting/2";

        acceptInvitationController.setSpeakerService(speakerService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(userWithRoleModerator);
        when(session.getAttribute("lastPageURI")).thenReturn(lastPageURI);
        when(req.getParameter("application")).thenReturn("7");

        acceptInvitationController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(lastPageURI);
    }
}