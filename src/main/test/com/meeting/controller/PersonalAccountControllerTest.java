package com.meeting.controller;

import com.meeting.entitiy.Meeting;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.MeetingService;
import com.meeting.service.SpeakerService;
import com.meeting.service.UserService;
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
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static util.Utils.*;

class PersonalAccountControllerTest {

    private PersonalAccountController personalAccountController;
    @Mock
    MeetingService meetingService;
    @Mock
    UserService userService;
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
        personalAccountController = new PersonalAccountController();
        meetingService = mock(MeetingService.class);
        userService = mock(UserService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        personalAccountController = null;
        userService = null;
        meetingService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldDisplayUserOwnAccount() throws UserNotFoundException, ServletException, IOException {
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        personalAccountController.setUserService(userService);
        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(createUser());
        when(userService.getUserById(4L)).thenReturn(createUser());
        when(req.getPathInfo()).thenReturn("/4");
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        personalAccountController.doGet(req, resp);

        verify(requestDispatcher, times(1)).forward(req, resp);
    }

    @Test
    void shouldThrowUserNotFoundException() throws UserNotFoundException, ServletException, IOException {
        String exceptionMessage = "Cannot get user by ID: 4";
        personalAccountController.setUserService(userService);
        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(createUser());
        when(req.getPathInfo()).thenReturn("/8");
        when(userService.getUserById(8L)).thenThrow(new UserNotFoundException(exceptionMessage, new SQLException()));

        personalAccountController.doGet(req, resp);

        verify(resp, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exceptionMessage);
    }

    @Test
    void shouldDisplaySpeakerAccount() throws UserNotFoundException, DataBaseException, ServletException, IOException {
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        SpeakerService speakerService = mock(SpeakerService.class);
        MeetingService meetingService = mock(MeetingService.class);

        personalAccountController.setUserService(userService);
        personalAccountController.setSpeakerService(speakerService);
        personalAccountController.setMeetingService(meetingService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(createUser());
        when(speakerService.getSpeakerById(228L)).thenReturn(createSpeaker());
        when(meetingService.getMeetingsSpeakerIsInvolvedIn(228L)).thenReturn(new HashSet<>(Collections.singletonList(new Meeting())));
        when(userService.getUserById(anyLong())).thenReturn(createUserWithRoleSpeaker());
        when(req.getPathInfo()).thenReturn("/4");
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        personalAccountController.doGet(req, resp);

        verify(requestDispatcher, times(1)).forward(req, resp);
    }
}