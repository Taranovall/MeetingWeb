package com.meeting.controller.meeting.application;

import com.meeting.service.MeetingService;
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
import static util.Constant.APPLICATION;
import static util.Constant.LAST_PAGE_URI;
import static util.Constant.LAST_PAGE_URI_ATTRIBUTE_NAME;
import static util.Constant.SPEAKER_ID;

class AcceptProposedTopicControllerTest {

    private AcceptProposedTopicController acceptProposedTopicController;
    @Mock
    MeetingService meetingService;
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
        acceptProposedTopicController = new AcceptProposedTopicController();
        meetingService = mock(MeetingService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        acceptProposedTopicController = null;
        session = null;
        meetingService = null;
        req = null;
        resp = null;
    }

    @Test
    void shouldAcceptProposedTopic() throws IOException, ServletException {
        acceptProposedTopicController.setMeetingService(meetingService);

        when(req.getSession()).thenReturn(session);
        when(req.getParameter(SPEAKER_ID)).thenReturn("9");
        when(session.getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn(LAST_PAGE_URI);
        when(req.getParameter(APPLICATION)).thenReturn("7");

        acceptProposedTopicController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(LAST_PAGE_URI);
    }
}