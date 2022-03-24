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
import static util.Constant.COUNT_OF_TOPICS_PARAM_NAME;
import static util.Constant.ERROR_ATTR_NAME;
import static util.Constant.LANGUAGE_ATTR_NAME;
import static util.Constant.TOPIC_NAME;
import static util.Constant.USER;
import static util.Util.createMeeting;
import static util.Util.createUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class CreateMeetingControllerTest {

    private static final String MEETING_NAME = "InvestPro Кипр Никосия 2022";
    private static final String MEETING_DATE = "2022-06-20";
    private static final String MEETING_START_TIME = "15:30";
    private static final String MEETING_END_TIME = "19:30";
    private static final String MEETING_PLACE = "Hilton Nicosia";
    private static final String ATTRIBUTE_NAME = "meeting";
    private static final String NOT_VALID_END_TIME = "14:30";
    private static final String TIME_IS_NOT_VALID = "Time isn't valid";
    private static final String PHOTO = "photo";
    private static final String IMG_NAME = "image.png";
    private static final String TOPICS_ARE_NOT_UNIQUE_ERROR = "Topic's name must be unique";
    private static final String NAME = "name";
    private static final String DATE = "date";
    private static final String END_TIME = "endTime";
    private static final String PLACE = "place";
    private static final String START_TIME = "startTime";

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
        when(req.getParameter(NAME)).thenReturn(MEETING_NAME);
        when(req.getParameter(DATE)).thenReturn(MEETING_DATE);
        when(req.getParameter(START_TIME)).thenReturn(MEETING_START_TIME);
        when(req.getParameter(END_TIME)).thenReturn(MEETING_END_TIME);
        when(req.getParameter(PLACE)).thenReturn(MEETING_PLACE);
        when(validationService.meetingMainInfoValidator(any(), eq(req))).thenReturn(true);
        when(req.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        createMeetingController.doGet(req, resp);

        verify(session, times(1)).setAttribute(eq(ATTRIBUTE_NAME), any());
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
        when(req.getParameter(NAME)).thenReturn(MEETING_NAME);
        when(req.getParameter(DATE)).thenReturn(MEETING_DATE);
        when(req.getParameter(START_TIME)).thenReturn(MEETING_START_TIME);
        when(req.getParameter(END_TIME)).thenReturn(NOT_VALID_END_TIME);
        when(req.getParameter(PLACE)).thenReturn(MEETING_PLACE);
        when(req.getRequestURI()).thenReturn("/moderator/create-meeting");
        when(req.getParameter(COUNT_OF_TOPICS_PARAM_NAME)).thenReturn("5");
        when(req.getRequestDispatcher(PATH_TO_CREATE_MEETING_FIRST_PAGE_JSP)).thenReturn(requestDispatcher);
        when(session.getAttribute(LANGUAGE_ATTR_NAME)).thenReturn("en");

        createMeetingController.doGet(req, resp);

        verify(session, times(1)).setAttribute(ERROR_ATTR_NAME, TIME_IS_NOT_VALID);
        verify(requestDispatcher, times(1)).forward(req, resp);
    }

    @Test
    void shouldCreateMeeting() throws ServletException, IOException {
        String[] topics = {"Topic1", "Topic2"};
        Part uploadedImage = mock(Part.class);

        createMeetingController.setMeetingService(meetingService);
        createMeetingController.setValidationService(validationService);

        when(req.getSession()).thenReturn(session);
        when(req.getParameterValues(TOPIC_NAME)).thenReturn(topics);
        when(session.getAttribute(ATTRIBUTE_NAME)).thenReturn(createObjectOfMeetingFromFirstPage());
        when(req.getPart(PHOTO)).thenReturn(uploadedImage);
        when(validationService.meetingPostValidator(any(), any(), any())).thenReturn(true);
        when(uploadedImage.getSubmittedFileName()).thenReturn(IMG_NAME);
        when(session.getAttribute(USER)).thenReturn(createUser());

        createMeetingController.doPost(req, resp);

        verify(resp, times(1)).sendRedirect("/");
    }

    @Test
    void shouldDisplaySecondPageWithErrorBecauseOfDuplicateNameOfTopics() throws ServletException, IOException {
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        String[] topics = {"Topic", "Topic"};
        Part uploadedImage = mock(Part.class);

        when(req.getSession()).thenReturn(session);
        when(req.getParameterValues(TOPIC_NAME)).thenReturn(topics);
        when(session.getAttribute(ATTRIBUTE_NAME)).thenReturn(createObjectOfMeetingFromFirstPage());
        when(req.getPart(PHOTO)).thenReturn(uploadedImage);
        when(uploadedImage.getSubmittedFileName()).thenReturn(IMG_NAME);
        when(uploadedImage.getSize()).thenReturn(1337L);
        when(session.getAttribute(USER)).thenReturn(createUser());
        when(req.getRequestDispatcher(PATH_TO_CREATE_MEETING_SECOND_PAGE_JSP)).thenReturn(requestDispatcher);
        when(session.getAttribute(LANGUAGE_ATTR_NAME)).thenReturn("en");

        createMeetingController.doPost(req, resp);

        verify(req, times(1)).setAttribute(ERROR_ATTR_NAME, TOPICS_ARE_NOT_UNIQUE_ERROR);
        verify(requestDispatcher, times(1)).forward(req, resp);
    }

    @Test
    private Meeting createObjectOfMeetingFromFirstPage() {
        Meeting meeting = createMeeting();
        meeting.setPhotoPath(null);
        return meeting;
    }
}