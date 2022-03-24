package com.meeting.controller.meeting;

import com.meeting.service.MeetingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import util.Constant;

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

class MarkPresentUsersControllerTest {

    private static final String LAST_PAGE_URI = "/meeting/4";

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
        String[] presentUsersId = {"4", "92", "12"};

        markPresentUsersController.setMeetingService(meetingService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn(LAST_PAGE_URI);
        when(req.getParameterValues(Constant.PRESENT_USER_ID)).thenReturn(presentUsersId);
        when(req.getPathInfo()).thenReturn("/4");

        markPresentUsersController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(LAST_PAGE_URI);
    }
}