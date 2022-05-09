package com.meeting.service.impl;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.TopicService;
import com.meeting.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import static com.meeting.util.Constant.QUERY_IS_NOT_VALID_ATTRIBUTE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Constant.COUNT_OF_TOPICS_PARAM_NAME;
import static util.Constant.LANGUAGE_ATTR_NAME;
import static util.Util.createUser;

public class ValidationServiceImplTest {

    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String PASSWORD_CONFIRM = "passwordConfirm";
    private static final String USER_LOGIN = "user";
    private static final String SPEAKER_LOGIN = "Spek";
    private final ValidationServiceImpl validationService = new ValidationServiceImpl();
    @Mock
    private static HttpServletRequest req;
    @Mock
    private static HttpSession session;
    @Mock
    private UserService userService;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(LANGUAGE_ATTR_NAME)).thenReturn("en");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void shouldSetAttributeLoginErrorTrue() {
        when(req.getParameter(LOGIN)).thenReturn("usesjdvhwg8493whfiuhvr");
        when(req.getParameter(PASSWORD)).thenReturn("askjdjakdfj22");
        when(req.getParameter(PASSWORD_CONFIRM)).thenReturn("askjdjakdfj22");

        validationService.registrationValidator(req);

        verify(req, times(1)).setAttribute("loginError", true);
    }

    @Test
    void ifUserDoesNotExistThenSetLoginErrorTrue() throws UserNotFoundException {
        when(req.getParameter(LOGIN)).thenReturn(USER_LOGIN);

        doReturn(null).when(userService).getUserByLogin(anyString());
        validationService.setUserService(userService);

        validationService.authValidator(req);

        verify(req, times(1)).setAttribute("loginError", true);

    }

    @Test
    void shouldSetAttributePasswordErrorTrue() throws UserNotFoundException {
        when(req.getParameter(LOGIN)).thenReturn(USER_LOGIN);
        when(req.getParameter(PASSWORD)).thenReturn("kladjfajdkf23");
        when(req.getParameter(PASSWORD_CONFIRM)).thenReturn("lfalffkal72");

        validationService.registrationValidator(req);

        User user = createUser();
        doReturn(user).when(userService).getUserByLogin(anyString());
        validationService.setUserService(userService);

        validationService.authValidator(req);

        verify(req, times(2)).setAttribute("passwordError", true);
    }

    @Test
    void shouldReturnNotNullUser() {
        when(req.getParameter(LOGIN)).thenReturn(USER_LOGIN);
        when(req.getParameter(PASSWORD)).thenReturn("lfalffkal72");
        when(req.getParameter(PASSWORD_CONFIRM)).thenReturn("lfalffkal72");

        User user = validationService.registrationValidator(req);

        assertNotNull(user);
    }

    @Test
    public void shouldReturnTrueIfUsersAreEquals() throws UserNotFoundException {
        User user = createUser();

        when(req.getParameter(LOGIN)).thenReturn(SPEAKER_LOGIN);
        when(req.getParameter(PASSWORD)).thenReturn("dd9422DNAN");
        doReturn(user).when(userService).getUserByLogin(anyString());

        validationService.setUserService(userService);

        User actualUser = validationService.authValidator(req);
        assertEquals(user, actualUser);
    }

    @Test
    public void searchValidator() {
        String query = "";
        validationService.searchValidator(query, req);
        when(req.getSession()).thenReturn(session);

        verify(session, times(1)).setAttribute(QUERY_IS_NOT_VALID_ATTRIBUTE_NAME, "Query cannot be empty");

        query = "Query in which more than 32 characters PPPPPP QQQ PPPPPP SSSSS PPPPP";
        validationService.searchValidator(query, req);

        verify(session, times(1)).setAttribute(QUERY_IS_NOT_VALID_ATTRIBUTE_NAME, "Query cannot be longer than 32 characters");

        query = "Valid query";
        boolean actual = validationService.searchValidator(query, req);

        assertTrue(actual);
    }

    @Test
    public void meetingMainInfoValidator() {
        Meeting meeting = createMeeting();

        when(req.getRequestURI()).thenReturn("/moderator/create-meeting");
        when(req.getParameter(COUNT_OF_TOPICS_PARAM_NAME)).thenReturn("2");

        boolean actual = validationService.meetingMainInfoValidator(meeting, req);
        assertFalse(actual);

        meeting.setTimeEnd("17:45");
        actual = validationService.meetingMainInfoValidator(meeting, req);
        assertTrue(actual);
    }

    @Test
    public void meetingPostValidator() {
        String[] topics = {"Topic1", "Topic2", "Topic3"};
        Part uploadedImage = mock(Part.class);

        when(uploadedImage.getSize()).thenReturn(228L);

        boolean actual = validationService.meetingPostValidator(topics, uploadedImage, req);

        assertTrue(actual);

        topics[2] = "";

        actual = validationService.meetingPostValidator(topics, uploadedImage, req);

        assertFalse(actual);
    }

    @Test
    public void proposingTopicsValidator() throws DataBaseException {
        String topicName = "topicName";

        TopicService topicService = mock(TopicService.class);
        when(topicService.isTopicExist(anyString())).thenReturn(false);
        validationService.setTopicService(topicService);

        boolean actual = validationService.proposingTopicsValidator(topicName, req);
        assertTrue(actual);

        topicName = Util.generateStringWithRandomChars(99);

        actual = validationService.proposingTopicsValidator(topicName, req);
        assertFalse(actual);
    }

    @Test
    public void chooseSpeakerValidator() {
        String speakerId = "none";
        boolean actual = validationService.chooseSpeakerValidator(speakerId, req);
        assertFalse(actual);

        speakerId = "992";
        actual = validationService.chooseSpeakerValidator(speakerId, req);
        assertTrue(actual);

    }

    @Test
    public void emailValidator() {
        String email = "ugabuga72@gmail.com";
        boolean actual = validationService.emailValidator(email, req);
        assertTrue(actual);

        email = "ugaBuga.gmail@com";
        actual = validationService.emailValidator(email, req);
        assertFalse(actual);
    }

    private Meeting createMeeting() {
        Meeting meeting = Util.createMeeting();
        meeting.setDate("2024-08-08");
        meeting.setTimeStart("17:40");
        meeting.setTimeEnd("17:30");
        meeting.setName("Hello World");
        return meeting;
    }
}