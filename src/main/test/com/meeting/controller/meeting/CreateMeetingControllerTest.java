package com.meeting.controller.meeting;

import com.meeting.entitiy.Meeting;
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
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.HashMap;

import static com.meeting.util.Constant.PATH_TO_CREATE_MEETING_FIRST_PAGE_JSP;
import static com.meeting.util.Constant.PATH_TO_CREATE_MEETING_SECOND_PAGE_JSP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static util.Utils.createMeeting;
import static util.Utils.createUser;

class CreateMeetingControllerTest {

    private CreateMeetingController createMeetingController;
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
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        createMeetingController = new CreateMeetingController();
        meetingService = mock(MeetingService.class);
        speakerService = mock(SpeakerService.class);
        validationService = mock(ValidationService.class);
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        createMeetingController = null;
        speakerService = null;
        validationService = null;
        meetingService = null;
        req = null;
        resp = null;
        session = null;
    }

    @Test
    void shouldCreateObjectOfMeetingAndForwardToSecondPage() throws ServletException, IOException {
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        HashMap parameterMap = mock(HashMap.class);
        createMeetingController.setValidationService(validationService);
        createMeetingController.setSpeakerService(speakerService);

        when(req.getSession()).thenReturn(session);
        when(req.getParameterMap()).thenReturn(parameterMap);
        when(parameterMap.size()).thenReturn(6);
        when(req.getParameter("name")).thenReturn("InvestPro Кипр Никосия 2022");
        when(req.getParameter("date")).thenReturn("2022-06-20");
        when(req.getParameter("startTime")).thenReturn("15:30");
        when(req.getParameter("endTime")).thenReturn("19:30");
        when(req.getParameter("place")).thenReturn("Hilton Nicosia");
        when(validationService.meetingMainInfoValidator(any(), eq(req))).thenReturn(true);
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        createMeetingController.doGet(req, resp);

        verify(session, times(1)).setAttribute(eq("meeting"), any());
        verify(requestDispatcher, times(1)).forward(req, resp);
    }

    @Test
    void shouldDisplayFirstPageWithErrorAfterSendingIncorrectDataToServer() throws ServletException, IOException {
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        HashMap parameterMap = mock(HashMap.class);

        createMeetingController.setSpeakerService(speakerService);

        when(req.getSession()).thenReturn(session);
        when(req.getParameterMap()).thenReturn(parameterMap);
        when(parameterMap.size()).thenReturn(6);
        when(req.getParameter("name")).thenReturn("InvestPro Кипр Никосия 2022");
        when(req.getParameter("date")).thenReturn("2022-06-20");
        when(req.getParameter("startTime")).thenReturn("15:30");
        when(req.getParameter("endTime")).thenReturn("14:30");
        when(req.getParameter("place")).thenReturn("Hilton Nicosia");
        when(req.getRequestURI()).thenReturn("/moderator/create-meeting");
        when(req.getParameter("countOfTopics")).thenReturn("5");
        when(req.getRequestDispatcher(PATH_TO_CREATE_MEETING_FIRST_PAGE_JSP)).thenReturn(requestDispatcher);

        createMeetingController.doGet(req, resp);

        verify(session, times(1)).setAttribute("error", "Time isn't valid");
        verify(requestDispatcher, times(1)).forward(req, resp);
    }

    @Test
    void shouldCreateMeeting() throws ServletException, IOException {
        String[] topics = {"Topic1", "Topic2"};
        Part uploadedImage = mock(Part.class);

        createMeetingController.setMeetingService(meetingService);
        createMeetingController.setValidationService(validationService);

        when(req.getSession()).thenReturn(session);
        when(req.getParameterValues("topicName")).thenReturn(topics);
        when(session.getAttribute("meeting")).thenReturn(createObjectOfMeetingFromFirstPage());
        when(req.getPart("photo")).thenReturn(uploadedImage);
        when(validationService.meetingPostValidator(any(), any(), any())).thenReturn(true);
        when(uploadedImage.getSubmittedFileName()).thenReturn("image.png");
        when(session.getAttribute("user")).thenReturn(createUser());

        createMeetingController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect("/");
    }

    @Test
    void shouldDisplaySecondPageWithErrorBecauseOfDuplicateNameOfTopics() throws ServletException, IOException {
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        String[] topics = {"Topic", "Topic"};
        Part uploadedImage = mock(Part.class);

        when(req.getSession()).thenReturn(session);
        when(req.getParameterValues("topicName")).thenReturn(topics);
        when(session.getAttribute("meeting")).thenReturn(createObjectOfMeetingFromFirstPage());
        when(req.getPart("photo")).thenReturn(uploadedImage);
        when(uploadedImage.getSubmittedFileName()).thenReturn("image.png");
        when(uploadedImage.getSize()).thenReturn(1337L);
        when(session.getAttribute("user")).thenReturn(createUser());
        when(req.getRequestDispatcher(PATH_TO_CREATE_MEETING_SECOND_PAGE_JSP)).thenReturn(requestDispatcher);

        createMeetingController.doPost(req, resp);

        verify(req, times(1)).setAttribute("error", "Topic's name must be unique");
        verify(requestDispatcher, times(1)).forward(req, resp);
    }

    @Test
    private Meeting createObjectOfMeetingFromFirstPage() {
        Meeting meeting = createMeeting();
        meeting.setPhotoPath(null);
        return meeting;
    }
}