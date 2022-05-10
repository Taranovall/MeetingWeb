package com.meeting.controller.meeting.application;

import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Constant.LAST_PAGE_URI;
import static util.Constant.LAST_PAGE_URI_ATTRIBUTE_NAME;
import static util.Constant.TOPIC_NAME;
import static util.Constant.USER;
import static util.Util.createUserWithRoleSpeaker;

class ProposeTopicControllerTest {

    private static final String NAME_OF_TOPIC = "seventeen";

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
    void shouldAcceptApplicationSentBySpeaker() throws IOException, ServletException, DataBaseException {
        User userWithRoleSpeaker = createUserWithRoleSpeaker();

        proposeTopicController.setMeetingService(meetingService);
        proposeTopicController.setValidationService(validationService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn(LAST_PAGE_URI);
        when(session.getAttribute(USER)).thenReturn(userWithRoleSpeaker);
        when(req.getParameter(TOPIC_NAME)).thenReturn(NAME_OF_TOPIC);
        when(validationService.proposingTopicsValidator(anyString(), anyLong(), any())).thenReturn(true);

        proposeTopicController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(LAST_PAGE_URI);
    }
}