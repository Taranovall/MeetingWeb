package com.meeting.controller;

import com.meeting.entitiy.Meeting;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.UserNotFoundException;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static util.Utils.createListWithMeetings;

class DisplayMeetingsControllerTest {

    private DisplayMeetingsController displayMeetingsController;
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
        displayMeetingsController = new DisplayMeetingsController();
        meetingService = mock(MeetingService.class);
        validationService = mock(ValidationService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        displayMeetingsController = null;
        validationService = null;
        meetingService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldDisplayMeetingsWhichIsGoingOnRightNow() throws UserNotFoundException, DataBaseException, ServletException, IOException {
        List<Meeting> meetings = createListWithMeetings();
        when(req.getSession()).thenReturn(session);
        when(req.getParameter("radioButton")).thenReturn("goingOnNow");
        when(meetingService.getAllMeetings()).thenReturn(meetings);

        displayMeetingsController.setMeetingService(meetingService);

        displayMeetingsController.doPost(req, resp);

        assertEquals(0, meetings.size());
    }

    @Test
    void shouldShowErrorMessage() throws ServletException, IOException, UserNotFoundException, DataBaseException {
        List<Meeting> meetings = createListWithMeetings();
        when(req.getSession()).thenReturn(session);
        when(req.getParameter("radioButton")).thenReturn(null);
        when(meetingService.getAllMeetings()).thenReturn(meetings);

        displayMeetingsController.setMeetingService(meetingService);

        displayMeetingsController.doPost(req, resp);

        verify(session, times(1)).setAttribute("error", "You didn't choose any option");
    }
}