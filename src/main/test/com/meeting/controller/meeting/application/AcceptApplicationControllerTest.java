package com.meeting.controller.meeting.application;

import com.meeting.entitiy.User;
import com.meeting.service.SpeakerService;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Constant.APPLICATION;
import static util.Constant.LAST_PAGE_URI;
import static util.Constant.LAST_PAGE_URI_ATTRIBUTE_NAME;
import static util.Constant.SPEAKER_ID;
import static util.Constant.USER;
import static util.Util.createUserWithRoleModerator;

class AcceptApplicationControllerTest {

    private AcceptApplicationController acceptApplicationController;
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
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        acceptApplicationController = new AcceptApplicationController();
        speakerService = mock(SpeakerService.class);
        validationService = mock(ValidationService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        acceptApplicationController = null;
        session = null;
        validationService = null;
        req = null;
        resp = null;
    }

    @Test
    void shouldAcceptApplicationSentBySpeaker() throws IOException, ServletException {
        User userWithRoleModerator = createUserWithRoleModerator();

        acceptApplicationController.setSpeakerService(speakerService);
        acceptApplicationController.setValidationService(validationService);

        when(req.getSession()).thenReturn(session);
        when(req.getParameter(SPEAKER_ID)).thenReturn("9");
        when(session.getAttribute(USER)).thenReturn(userWithRoleModerator);
        when(session.getAttribute(LAST_PAGE_URI_ATTRIBUTE_NAME)).thenReturn(LAST_PAGE_URI);
        when(validationService.chooseSpeakerValidator(anyString(), eq(req))).thenReturn(true);
        when(req.getParameter(APPLICATION)).thenReturn("7");

        acceptApplicationController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect(LAST_PAGE_URI);
    }
}