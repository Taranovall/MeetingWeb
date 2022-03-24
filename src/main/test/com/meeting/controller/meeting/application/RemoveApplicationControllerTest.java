package com.meeting.controller.meeting.application;

import com.meeting.entitiy.User;
import com.meeting.service.SpeakerService;
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
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Constant.APPLICATION;
import static util.Constant.LAST_PAGE_URI;
import static util.Constant.LAST_PAGE_URI_ATTRIBUTE_NAME;
import static util.Constant.SENT_APPLICATION_LIST;
import static util.Constant.USER;
import static util.Util.createUserWithRoleModerator;

class RemoveApplicationControllerTest {

    private RemoveApplicationController removeApplicationController;
    @Mock
    SpeakerService speakerService;
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
        removeApplicationController = new RemoveApplicationController();
        speakerService = mock(SpeakerService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        removeApplicationController = null;
        session = null;
        req = null;
        resp = null;
    }

    @Test
    void shouldAcceptApplicationSentBySpeaker() throws IOException, ServletException {
        User userWithRoleModerator = createUserWithRoleModerator();

        removeApplicationController.setSpeakerService(speakerService);

        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(USER)).thenReturn(userWithRoleModerator);
        when(session.getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn(LAST_PAGE_URI);
        when(req.getParameter(APPLICATION)).thenReturn("7");
        when(session.getAttribute(SENT_APPLICATION_LIST)).thenReturn(new ArrayList<>(Arrays.asList("7","8","9")));

        removeApplicationController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(LAST_PAGE_URI);
    }
}