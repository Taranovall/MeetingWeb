package com.meeting.controller;

import com.meeting.service.UserService;
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

import static org.mockito.Mockito.*;
import static util.Util.createListWithMeetings;

class SortMeetingControllerTest {

    private static final String SORT_METHOD = "sortMethod";
    private static final String MEETINGS = "meetings";
    private static final String IS_FORM_HAS_BEEN_USED = "isFormHasBeenUsed";

    private SortMeetingController sortMeetingController;
    @Mock
    UserService userService;
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
        sortMeetingController = new SortMeetingController();
        userService = mock(UserService.class);
        validationService = mock(ValidationService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        sortMeetingController = null;
        validationService = null;
        userService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldDisplayPageWithSortedMeetings() throws IOException, ServletException {
        String sortMethod = "date";
        when(req.getSession()).thenReturn(session);
        when(req.getParameter(SORT_METHOD)).thenReturn(sortMethod);
        when(session.getAttribute(MEETINGS)).thenReturn(createListWithMeetings());

        sortMeetingController.doPost(req, resp);

        verify(session, times(1)).setAttribute(IS_FORM_HAS_BEEN_USED, sortMethod);
        verify(resp, times(1)).sendRedirect("/");
    }
}