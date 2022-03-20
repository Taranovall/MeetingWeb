package com.meeting.controller.meeting;

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

import static org.mockito.Mockito.*;

class MarkPresentUsersControllerTest {

    private MarkPresentUsersController markPresentUsersController;
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
        markPresentUsersController = new MarkPresentUsersController();
        meetingService = mock(MeetingService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        markPresentUsersController = null;
        meetingService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldMarkPresentUsers() throws IOException, ServletException {
        String lastURI = "/meeting/4";
        String[] presentUsersId = {"4", "92", "12"};

        markPresentUsersController.setMeetingService(meetingService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("lastPageURI")).thenReturn(lastURI);
        when(req.getParameterValues("presentUserId")).thenReturn(presentUsersId);
        when(req.getPathInfo()).thenReturn("/4");

        markPresentUsersController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(lastURI);
    }
}