package com.meeting.controller.meeting.application;

import com.meeting.entitiy.User;
import com.meeting.service.SpeakerService;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static util.Utils.createUserWithRoleModerator;

class AcceptApplicationControllerTest {

    private AcceptApplicationController acceptApplicationController;
    @Mock
    SpeakerService speakerService;
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
        acceptApplicationController = new AcceptApplicationController();
        speakerService = mock(SpeakerService.class);
        validationService = mock(ValidationService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        acceptApplicationController = null;
        session = null;
        validationService = null;
        req = null;
        resp = null;
    }

    @Test
    void shouldAcceptApplicationSentBySpeaker() throws IOException, ServletException {
        User userWithRoleModerator = createUserWithRoleModerator();
        String lastPageURI = "/meeting/2";

        acceptApplicationController.setSpeakerService(speakerService);
        acceptApplicationController.setValidationService(validationService);

        when(req.getSession()).thenReturn(session);
        when(req.getParameter("speakerId")).thenReturn("9");
        when(session.getAttribute("user")).thenReturn(userWithRoleModerator);
        when(session.getAttribute("lastPageURI")).thenReturn(lastPageURI);
        when(validationService.chooseSpeakerValidator(anyString(), eq(req))).thenReturn(true);
        when(req.getParameter("application")).thenReturn("7");

        acceptApplicationController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(lastPageURI);
    }
}