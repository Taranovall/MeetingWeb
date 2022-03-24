package com.meeting.controller.meeting;

import com.meeting.service.MeetingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Constant.LAST_PAGE_URI_ATTRIBUTE_NAME;
import static util.Constant.MEETING;

class EditMeetingControllerTest {

    private static final String MEETING_START_TIME = "meetingStartTime";
    private static final String MEETING_END_TIME = "meetingEndTime";
    private static final String MEETING_DATE = "meetingDate";
    private static final String MEETING_PLACE = "meetingPlace";
    private static final String COUNT_OF_TOPICS = "countOfTopics";
    private static final String LAST_PAGE_URI = "/meeting/4";
    private EditMeetingController editMeetingController;
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
        editMeetingController = new EditMeetingController();
        meetingService = mock(MeetingService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        editMeetingController = null;
        meetingService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldEditMeetingInformation() throws IOException, ServletException {
        editMeetingController.setMeetingService(meetingService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn(LAST_PAGE_URI);
        when(session.getAttribute(MEETING)).thenReturn(Util.createMeeting());
        when(req.getParameter(MEETING_START_TIME)).thenReturn("14:20");
        when(req.getParameter(MEETING_END_TIME)).thenReturn("16:20");
        when(req.getParameter(MEETING_DATE)).thenReturn("2022-06-21");
        when(req.getParameter(MEETING_PLACE)).thenReturn("14:20");
        when(req.getRequestURI()).thenReturn("/moderator/create-meeting");
        when(req.getParameter(COUNT_OF_TOPICS)).thenReturn("5");

        editMeetingController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(LAST_PAGE_URI);
    }
}