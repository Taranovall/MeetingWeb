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

import static com.meeting.util.Constant.*;
import static org.mockito.Mockito.*;
import static util.Utils.createListWithMeetings;

class SortMeetingControllerTest {

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
        when(req.getParameter(SORT_METHOD_ATTRIBUTE_NAME)).thenReturn(sortMethod);
        when(session.getAttribute(MEETING_ATTRIBUTE_NAME)).thenReturn(createListWithMeetings());

        sortMeetingController.doPost(req, resp);

        verify(session, times(1)).setAttribute(IS_FORM_HAS_BEEN_USED_ATTRIBUTE_NAME, sortMethod);
        verify(resp, times(1)).sendRedirect("/");
    }
}