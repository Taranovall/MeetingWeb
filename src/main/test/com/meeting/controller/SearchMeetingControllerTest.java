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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static util.Utils.createListWithMeetings;

class SearchMeetingControllerTest {

    private SearchMeetingController searchMeetingController;
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
        searchMeetingController = new SearchMeetingController();
        meetingService = mock(MeetingService.class);
        validationService = mock(ValidationService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        searchMeetingController = null;
        validationService = null;
        meetingService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldExecuteSearchQuery() throws UserNotFoundException, DataBaseException, ServletException, IOException {
        List<Meeting> meetings = createListWithMeetings();

        searchMeetingController.setMeetingService(meetingService);
        searchMeetingController.setValidationService(validationService);

        when(req.getSession()).thenReturn(session);
        when(req.getParameter("query")).thenReturn("Meet");
        when(meetingService.getAllMeetings()).thenReturn(meetings);
        when(validationService.searchValidator(anyString(),eq(req))).thenReturn(true);

        searchMeetingController.doPost(req, resp);

        assertEquals(1, meetings.size());
    }
}