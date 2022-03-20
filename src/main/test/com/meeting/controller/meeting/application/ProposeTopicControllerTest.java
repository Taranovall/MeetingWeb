package com.meeting.controller.meeting.application;

import com.meeting.entitiy.User;
import com.meeting.service.MeetingService;
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

import static org.mockito.Mockito.*;
import static util.Utils.createUserWithRoleSpeaker;

class ProposeTopicControllerTest {

    private ProposeTopicController proposeTopicController;
    @Mock
    MeetingService meetingService;
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
        proposeTopicController = new ProposeTopicController();
        meetingService = mock(MeetingService.class);
        validationService = mock(ValidationService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        proposeTopicController = null;
        session = null;
        meetingService = null;
        validationService = null;
        req = null;
        resp = null;
    }

    @Test
    void shouldAcceptApplicationSentBySpeaker() throws IOException, ServletException {
        User userWithRoleSpeaker = createUserWithRoleSpeaker();
        String lastPageURI = "/meeting/2";

        proposeTopicController.setMeetingService(meetingService);
        proposeTopicController.setValidationService(validationService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("lastPageURI")).thenReturn(lastPageURI);
        when(session.getAttribute("user")).thenReturn(userWithRoleSpeaker);
        when(req.getParameter("topicName")).thenReturn("seventeen");
        when(validationService.proposingTopicsValidator(anyString(), any())).thenReturn(true);

        proposeTopicController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(lastPageURI);
    }
}