package com.meeting.controller;

import com.meeting.exception.DataBaseException;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.MeetingService;
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
import java.util.Collections;
import java.util.LinkedList;

import static com.meeting.util.Constant.IS_FORM_HAS_BEEN_USED_ATTRIBUTE_NAME;
import static com.meeting.util.Constant.SORT_METHOD_ATTRIBUTE_NAME;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Util.createMeeting;

class MainPageControllerTest {

    private static final String PARTICIPANTS = "participants";
    @Mock
    MeetingService meetingService;
    private MainPageController mainPageController;
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
        mainPageController = new MainPageController();
        meetingService = mock(MeetingService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        mainPageController = null;
        meetingService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldForwardToMainPage() throws UserNotFoundException, DataBaseException, ServletException, IOException {
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        mainPageController.setMeetingService(meetingService);

        when(req.getSession()).thenReturn(session);
        when(meetingService.getAllMeetings()).thenReturn(new LinkedList<>(Collections.singletonList(createMeeting())));
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        mainPageController.doGet(req, resp);

        verify(requestDispatcher, times(1)).forward(req, resp);
    }

    @Test
    void shouldForwardToSecondPageWithMeetings() throws UserNotFoundException, DataBaseException, ServletException, IOException {
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        mainPageController.setMeetingService(meetingService);

        when(req.getSession()).thenReturn(session);
        when(meetingService.getAllMeetings()).thenReturn(new LinkedList<>(Collections.singletonList(createMeeting())));
        when(req.getParameter("page")).thenReturn("4");
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        mainPageController.doGet(req, resp);

        verify(requestDispatcher, times(1)).forward(req, resp);
    }

    @Test
    void shouldForwardToMainPageWithSortedMeetingsByParticipantsInReverseOrder() throws UserNotFoundException, DataBaseException, ServletException, IOException {
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        mainPageController.setMeetingService(meetingService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(IS_FORM_HAS_BEEN_USED_ATTRIBUTE_NAME)).thenReturn(PARTICIPANTS);
        when(session.getAttribute(SORT_METHOD_ATTRIBUTE_NAME)).thenReturn(PARTICIPANTS);
        when(meetingService.getAllMeetings()).thenReturn(new LinkedList<>(Collections.singletonList(createMeeting())));
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        mainPageController.doGet(req, resp);

        verify(requestDispatcher, times(1)).forward(req, resp);
    }

    @Test
    void shouldForwardToMainPageWithSortedMeetingsByParticipantsInOrdinaryOrder() throws UserNotFoundException, DataBaseException, ServletException, IOException {
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        mainPageController.setMeetingService(meetingService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(IS_FORM_HAS_BEEN_USED_ATTRIBUTE_NAME)).thenReturn(PARTICIPANTS);
        when(session.getAttribute(SORT_METHOD_ATTRIBUTE_NAME)).thenReturn(null);
        when(meetingService.getAllMeetings()).thenReturn(new LinkedList<>(Collections.singletonList(createMeeting())));
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        mainPageController.doGet(req, resp);

        verify(requestDispatcher, times(1)).forward(req, resp);
    }

    @Test
    void shouldThrowDataBaseExceptionWhenForwardingToMainPage() throws UserNotFoundException, DataBaseException, ServletException, IOException {
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        mainPageController.setMeetingService(meetingService);

        when(req.getSession()).thenReturn(session);
        when(meetingService.getAllMeetings()).thenReturn(new LinkedList<>(Collections.singletonList(createMeeting())));
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        mainPageController.doGet(req, resp);

        verify(requestDispatcher, times(1)).forward(req, resp);
    }
}