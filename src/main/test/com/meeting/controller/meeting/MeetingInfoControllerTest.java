package com.meeting.controller.meeting;

import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.MeetingService;
import com.meeting.service.SpeakerService;
import com.meeting.service.ValidationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.meeting.entitiy.Role.MODERATOR;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static util.Utils.*;

class MeetingInfoControllerTest {

    private MeetingInfoController meetingInfoController;
    @Mock
    MeetingService meetingService;
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
    private RequestDispatcher requestDispatcher;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        meetingInfoController = new MeetingInfoController();
        meetingService = mock(MeetingService.class);
        speakerService = mock(SpeakerService.class);
        validationService = mock(ValidationService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        requestDispatcher = mock(RequestDispatcher.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        meetingInfoController = null;
        speakerService = null;
        validationService = null;
        meetingService = null;
        req = null;
        resp = null;
        session = null;
        requestDispatcher = null;
    }

    @Test
    void showMeetingInfoToUser() throws UserNotFoundException, DataBaseException, ServletException, IOException {
        User user = createUser();
        user.setMeetingIdsSetUserTakesPart(new ArrayList<>(Collections.singletonList(2L)));

        meetingInfoController.setMeetingService(meetingService);

        when(req.getSession()).thenReturn(session);
        when(req.getPathInfo()).thenReturn("/2");
        when(session.getAttribute("user")).thenReturn(user);
        when(meetingService.getMeetingById(anyLong())).thenReturn(createMeeting());
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        meetingInfoController.doGet(req, resp);

        verify(req, times(1)).setAttribute("participating", true);
    }

    @Test
    void showMeetingInfoToSpeaker() throws UserNotFoundException, DataBaseException, ServletException, IOException {
        User user = createUserWithRoleSpeaker();
        user.setMeetingIdsSetUserTakesPart(new ArrayList<>(Collections.singletonList(2L)));

        meetingInfoController.setMeetingService(meetingService);
        meetingInfoController.setSpeakerService(speakerService);

        when(req.getSession()).thenReturn(session);
        when(req.getPathInfo()).thenReturn("/2");
        when(session.getAttribute("user")).thenReturn(user);
        when(meetingService.getMeetingById(anyLong())).thenReturn(createMeeting());
        when(speakerService.getSentApplications(anyLong())).thenReturn(new ArrayList<>(Arrays.asList("7", "9")));
        when(speakerService.getReceivedApplications(anyLong())).thenReturn(new ArrayList<>(Arrays.asList("10", "11")));
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        meetingInfoController.doGet(req, resp);

        verify(session, times(1)).setAttribute(eq("sentApplicationList"), any());
        verify(session, times(1)).setAttribute(eq("receivedApplicationList"), any());
    }

    @Test
    void showMeetingInfoToModerator() throws UserNotFoundException, DataBaseException, ServletException, IOException {
        User user = createUser();
        user.setRole(MODERATOR);
        user.setMeetingIdsSetUserTakesPart(new ArrayList<>(Collections.singletonList(2L)));

        meetingInfoController.setMeetingService(meetingService);
        meetingInfoController.setSpeakerService(speakerService);

        when(req.getSession()).thenReturn(session);
        when(req.getPathInfo()).thenReturn("/2");
        when(session.getAttribute("user")).thenReturn(user);
        when(meetingService.getMeetingById(anyLong())).thenReturn(createMeeting());
        when(speakerService.getSentApplications(anyLong())).thenReturn(new ArrayList<>(Arrays.asList("7", "9")));
        when(speakerService.getReceivedApplications(anyLong())).thenReturn(new ArrayList<>(Arrays.asList("10", "11")));
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        meetingInfoController.doGet(req, resp);

        verify(req, times(1)).setAttribute("presentUser", meetingService.getPresentUserIds(anyLong()));
    }
}