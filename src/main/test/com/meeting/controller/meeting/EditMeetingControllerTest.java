package com.meeting.controller.meeting;

import com.meeting.service.MeetingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import util.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.*;

class EditMeetingControllerTest {

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
        String lastPageURI = "/meeting/4";

        editMeetingController.setMeetingService(meetingService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("lastPageURI")).thenReturn(lastPageURI);
        when(session.getAttribute("meeting")).thenReturn(Utils.createMeeting());
        when(req.getParameter("meetingStartTime")).thenReturn("14:20");
        when(req.getParameter("meetingEndTime")).thenReturn("16:20");
        when(req.getParameter("meetingDate")).thenReturn("2022-06-21");
        when(req.getParameter("meetingPlace")).thenReturn("14:20");
        when(req.getRequestURI()).thenReturn("/moderator/create-meeting");
        when(req.getParameter("countOfTopics")).thenReturn("5");

        editMeetingController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(lastPageURI);
    }
}