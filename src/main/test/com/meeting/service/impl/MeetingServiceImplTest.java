package com.meeting.service.impl;

import com.meeting.dao.MeetingDao;
import com.meeting.dao.TopicDao;
import com.meeting.dao.impl.MeetingDaoImpl;
import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Speaker;
import com.meeting.entitiy.Topic;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.EmailException;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.SpeakerService;
import com.meeting.service.TopicService;
import com.meeting.service.UserService;
import com.meeting.connection.ConnectionPool;
import com.meeting.util.Constant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.meeting.util.SQLQuery.CANCEL_PROPOSED_TOPIC_SQL;
import static com.meeting.util.SQLQuery.CREATE_TOPIC_SQL;
import static com.meeting.util.SQLQuery.GET_ALL_SPEAKER_BY_MEETING_ID_SQL;
import static com.meeting.util.SQLQuery.GET_PRESENT_USERS_SQL;
import static com.meeting.util.SQLQuery.PROPOSE_TOPIC_SQL;
import static com.meeting.util.SQLQuery.UPDATE_MEETING_INFORMATION_SQL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Constant.LANGUAGE_ATTR_NAME;
import static util.Util.createSpeaker;
import static util.Util.createUser;

class MeetingServiceImplTest {

    private static final String SPEAKER_LOGIN = "SpeakerWithoutName";
    private static final String NO_SPEAKER = "none";
    private static final String[] TOPICS = {"Topic1", "Topic2"};
    private static final String RESULT_SET_ID = "id";
    private static final String RESULT_SET_NAME = "name";
    private static final String RESULT_SET_DATE = "date";
    private static final String RESULT_SET_TIME_START = "time_start";
    private static final String RESULT_SET_TIME_END = "time_end";
    private static final String RESULT_SET_PLACE = "place";
    private static final String RESULT_SET_PHOTO_PATH = "photo_path";
    private static final String RESULT_SET_SPEAKER_ID = "speaker_id";
    private static final String RESULT_SET_TOPIC_ID = "topic_id";
    private static final String CYPRUS_NAME = "InvestPro Кипр Никосия 2022";
    private static final String CYPRYS_DATE = "2022-06-20";
    private static final String CYPRUS_TIME_START = "15:30";
    private static final String CYPRUS_TIME_END = "16:30";
    private static final String CYPRUS_PLACE = "Hilton Nicosia";
    private static final String CYPRUS_PHOTO_PATH = "/image/123qq.jpeg";
    private static final String KYIV_NAME = "Invest Pro Украина Киев 2022";
    private static final String KYIV_DATE = "2022-09-20";
    private static final String KYIV_TIME_START = "19:40";
    private static final String KYIV_TIME_END = "22:31";
    private static final String KYIV_PLACE = "Hilton Kyiv";
    private static final String KYIV_PHOTO_PATH = "/image/123Kyiv.jpeg";
    @Mock
    private ResultSet rs;
    @Mock
    private Connection c;
    @Mock
    private AutoCloseable closeable;
    private PreparedStatement p;
    private MeetingServiceImpl meetingService;

    private static ConnectionPool connectionPool;
    private static MockedStatic<ConnectionPool> mocked;

    @BeforeAll
    static void beforeAll() {
        connectionPool = mock(ConnectionPool.class);
        mocked = mockStatic(ConnectionPool.class);
        mocked.when(ConnectionPool::getInstance).thenReturn(connectionPool);
    }

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        c = mock(Connection.class);
        when(ConnectionPool.getInstance().getConnection()).thenReturn(c);
        when(connectionPool.getConnection()).thenReturn(c);
        meetingService = new MeetingServiceImpl();
        p = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);

    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        c = null;
        p = null;
        meetingService = null;
    }

    @AfterAll
    static void afterAll() {
        connectionPool = null;
        mocked.close();
    }

    @Test
    void shouldCreateMeetingAndMakeCommit() throws DataBaseException, SQLException, UserNotFoundException {
        UserService userService = mock(UserService.class);
        meetingService.setUserService(userService);
        when(userService.getUserByLogin(SPEAKER_LOGIN)).thenReturn(new User(7L, SPEAKER_LOGIN));
        User user = createUser();
        Meeting meeting = createFirstMeeting();
        String[] speakers = {SPEAKER_LOGIN, NO_SPEAKER};

        File image = new File("img.jpeg");

        when(rs.next()).thenReturn(true);
        when(c.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(p);
        when(c.prepareStatement(anyString())).thenReturn(p);
        when(p.executeUpdate()).thenReturn(1);
        when(p.getGeneratedKeys()).thenReturn(rs);

        meetingService.createMeeting(user, meeting, TOPICS, speakers, image);

        verify(c, times(1)).commit();
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotCreateMeeting() throws SQLException, UserNotFoundException {
        User user = createUser();
        Meeting meeting = createFirstMeeting();
        String[] speakers = {SPEAKER_LOGIN, NO_SPEAKER};

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        TopicDao topicDao = mock(TopicDao.class);
        UserService userService = mock(UserService.class);
        MeetingDao meetingDao = mock(MeetingDao.class);

        when(userService.getUserByLogin(SPEAKER_LOGIN)).thenReturn(new User(7L, SPEAKER_LOGIN));
        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(LANGUAGE_ATTR_NAME)).thenReturn("en");
        doThrow(SQLException.class).when(topicDao).addTopicsToMeeting(anyLong(), any(), eq(c));

        meetingService.setTopicDao(topicDao);
        meetingService.setUserService(userService);
        meetingService.setMeetingDao(meetingDao);

        File image = new File("img.png");
        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.createMeeting(user, meeting, TOPICS, speakers, image));

        String expected = "Cannot create meeting";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnMeetingById() throws SQLException, DataBaseException, UserNotFoundException {
        TopicService topicService = mock(TopicService.class);
        SpeakerService speakerService = mock(SpeakerService.class);
        MeetingDao meetingDao = mock(MeetingDaoImpl.class);
        when(rs.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(rs.getLong(RESULT_SET_ID)).thenReturn(2L);
        when(rs.getString(RESULT_SET_NAME)).thenReturn(CYPRUS_NAME);
        when(rs.getString(RESULT_SET_DATE)).thenReturn(CYPRYS_DATE);
        when(rs.getString(RESULT_SET_TIME_START)).thenReturn(CYPRUS_TIME_START);
        when(rs.getString(RESULT_SET_TIME_END)).thenReturn(CYPRUS_TIME_END);
        when(rs.getString(RESULT_SET_PLACE)).thenReturn(CYPRUS_PLACE);
        when(rs.getString(RESULT_SET_PHOTO_PATH)).thenReturn(CYPRUS_PHOTO_PATH);
        when(rs.getLong(RESULT_SET_SPEAKER_ID)).thenReturn(7L);
        when(rs.getLong(RESULT_SET_TOPIC_ID)).thenReturn(9L);
        when(c.prepareStatement(anyString())).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);

        meetingService.setTopicService(topicService);
        meetingService.setSpeakerService(speakerService);
        meetingService.setMeetingDao(meetingDao);
        when(meetingDao.getById(anyLong(), eq(c))).thenCallRealMethod();
        when(topicService.getAllFreeTopicsByMeetingId(2L)).thenReturn(new HashSet<>(Collections.singletonList(new Topic(1L, Util.generateStringWithRandomChars(10)))));
        when(speakerService.getSpeakerById(37L)).thenReturn(new Speaker(7L, "Speaker"));
        when(topicService.getById(anyLong()))
                .thenReturn(new Topic(9L, Util.generateStringWithRandomChars(9)))
                .thenReturn(new Topic(13L, Util.generateStringWithRandomChars(9)));

        when(meetingDao.getSentApplicationsByMeetingId(2L, c)).thenCallRealMethod();
        when(rs.getLong(RESULT_SET_TOPIC_ID)).thenReturn(25L);
        when(rs.getLong(RESULT_SET_SPEAKER_ID))
                .thenReturn(8L)
                .thenReturn(15L);
        Map<Long, Long> proposedTopics = new HashMap<>();
        proposedTopics.put(99L, 12L);
        proposedTopics.put(98L, 13L);
        when(meetingDao.getProposedTopicsBySpeakerByMeetingId(2L, c)).thenReturn(proposedTopics);

        Map<Long, Long> topicSpeakerIdMap = new HashMap<>();
        topicSpeakerIdMap.put(9L, 37L);
        topicSpeakerIdMap.put(13L, 37L);
        when(meetingDao.getTopicSpeakerIdMapByMeetingId(anyLong(), eq(c))).thenReturn(topicSpeakerIdMap);

        meetingService.getMeetingById(2L);
    }

    @Test
    void shouldReturnDataBaseExceptionWithMessage_MeetingDoesNotExist() throws SQLException {
        MeetingDao meetingDao = mock(MeetingDao.class);
        meetingService.setMeetingDao(meetingDao);
        when(meetingDao.getById(anyLong(), eq(c))).thenReturn(Optional.empty());

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.getMeetingById(4L));

        String expected = Constant.MEETING_WITH_THIS_ID_NOT_EXIST + "4";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldProposeTopicAndReturnTrue() throws SQLException, DataBaseException {
        when(c.prepareStatement(PROPOSE_TOPIC_SQL)).thenReturn(p);

        when(rs.next()).thenReturn(true);
        when(rs.getLong(1)).thenReturn(18L);
        when(c.prepareStatement(CREATE_TOPIC_SQL, Statement.RETURN_GENERATED_KEYS)).thenReturn(p);
        when(p.executeUpdate()).thenReturn(1);
        when(p.getGeneratedKeys()).thenReturn(rs);

        boolean result = meetingService.proposeTopic(1L, 4L, "TopicName");
        assertTrue(result);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotProposeTopic() throws SQLException {
        TopicDao topicDao = mock(TopicDao.class);
        meetingService.setTopicDao(topicDao);
        doThrow(SQLException.class).when(topicDao).save(any(), eq(c));

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.proposeTopic(4L, 10L, Util.generateStringWithRandomChars(15)));

        String expected = "Cannot propose topic";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldAcceptProposedTopicAndReturnTrue() throws SQLException, DataBaseException {
        when(c.prepareStatement(anyString())).thenReturn(p);

        boolean result = meetingService.acceptProposedTopic(3L, 2L, 2L);
        assertTrue(result);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotAcceptProposedTopic() throws SQLException {
        MeetingDao meetingDao = mock(MeetingDao.class);
        meetingService.setMeetingDao(meetingDao);
        doThrow(SQLException.class).when(meetingDao).acceptProposedTopics(anyLong(), anyLong(), anyLong(), any());

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.acceptProposedTopic(4L, 10L, 29L));

        String expected = "Cannot accept proposed topic";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldCancelProposedTopicsAndReturnTrue() throws SQLException, DataBaseException {
        when(c.prepareStatement(CANCEL_PROPOSED_TOPIC_SQL)).thenReturn(p);

        boolean result = meetingService.cancelProposedTopic(13L);
        assertTrue(result);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotCancelProposedTopic() throws SQLException {
        MeetingDao meetingDao = mock(MeetingDao.class);
        meetingService.setMeetingDao(meetingDao);
        doThrow(SQLException.class).when(meetingDao).cancelProposedTopic(anyLong(), any());

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.cancelProposedTopic(4L));

        String expected = "Cannot cancel proposed topics";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnAllMeetings() throws SQLException, UserNotFoundException, DataBaseException {
        TopicService topicService = mock(TopicService.class);
        meetingService.setTopicService(topicService);
        when(rs.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        when(rs.getLong(RESULT_SET_ID)).thenReturn(2L).thenReturn(7L);
        when(rs.getString(RESULT_SET_NAME)).thenReturn(CYPRUS_NAME).thenReturn(KYIV_NAME);
        when(rs.getString(RESULT_SET_DATE)).thenReturn(CYPRYS_DATE).thenReturn(KYIV_DATE);
        when(rs.getString(RESULT_SET_TIME_START)).thenReturn(CYPRUS_TIME_START).thenReturn(KYIV_TIME_START);
        when(rs.getString(RESULT_SET_TIME_END)).thenReturn(CYPRUS_TIME_END).thenReturn(KYIV_TIME_END);
        when(rs.getString(RESULT_SET_PLACE)).thenReturn(CYPRUS_PLACE).thenReturn(KYIV_PLACE);
        when(rs.getString(RESULT_SET_PHOTO_PATH)).thenReturn(CYPRUS_PHOTO_PATH).thenReturn(KYIV_PHOTO_PATH);

        when(c.prepareStatement(anyString())).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);

        int expectedSize = 2;
        int actualSize = meetingService.getAllMeetings().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotGetAllMeeting() throws SQLException {
        MeetingDao meetingDao = mock(MeetingDao.class);
        meetingService.setMeetingDao(meetingDao);
        when(meetingDao.getAll(c)).thenThrow(SQLException.class);

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.getAllMeetings());

        String expected = "Cannot get all meetings";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldUpdateMeetingInformation() throws SQLException, UserNotFoundException, EmailException, DataBaseException {
        MeetingDao meetingDao = mock(MeetingDaoImpl.class);
        UserService userService = mock(UserService.class);
        meetingService.setMeetingDao(meetingDao);
        meetingService.setUserService(userService);

        when(meetingDao.getSpeakerIdsByMeetingId(anyLong(), eq(c))).thenReturn(new HashSet<>(Arrays.asList(22L)));

        when(rs.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        when(rs.getLong(RESULT_SET_SPEAKER_ID))
                .thenReturn(99L)
                .thenReturn(98L)
                .thenReturn(97L);

        when(userService.getUserById(anyLong()))
                .thenReturn(new User("___firstEmail@gmail.com", "First", 99L))
                .thenReturn(new User("___secondEmail@gmail.com", "Second", 98L))
                .thenReturn(new User("___ThirdEmail@gmail.com", "Third", 97L));

        when(c.prepareStatement(anyString())).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);
        doCallRealMethod().when(meetingDao).updateInformation(any(), eq(c));
        when(c.prepareStatement(UPDATE_MEETING_INFORMATION_SQL)).thenReturn(p);
        when(p.executeUpdate()).thenReturn(1);

        Meeting oldMeeting = createFirstMeeting();
        Meeting newMeeting = createSecondMeeting();

        meetingService.updateInformation(newMeeting, oldMeeting);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotUpdateInformation() throws SQLException {
        MeetingDao meetingDao = mock(MeetingDao.class);
        meetingService.setMeetingDao(meetingDao);
        doThrow(SQLException.class).when(meetingDao).updateInformation(any(), eq(c));

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.updateInformation(createFirstMeeting(), createSecondMeeting()));

        String expected = "Cannot update information";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);

    }

    @Test
    void shouldReturnSetWithMeetings() throws SQLException, UserNotFoundException, DataBaseException {
        MeetingDao meetingDao = mock(MeetingDaoImpl.class);
        meetingService.setMeetingDao(meetingDao);

        when(meetingDao.getById(anyLong(), eq(c))).thenReturn(Optional.of(createFirstMeeting()));
        when(meetingDao.getAllMeetingsIdSpeakerInvolvesIn(anyLong(), eq(c))).thenCallRealMethod();
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getLong("meeting_id")).thenReturn(9L);
        when(c.prepareStatement(anyString())).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);

        int expectedSize = 1;
        int actualSize = meetingService.getMeetingsSpeakerIsInvolvedIn(22L).size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void shouldReturnDataBaseExceptionWithMessage_CannotGetMeetingsInWhichSpeakerIsInvolved() throws SQLException {
        MeetingDao meetingDao = mock(MeetingDaoImpl.class);
        meetingService.setMeetingDao(meetingDao);

        when(meetingDao.getAllMeetingsIdSpeakerInvolvesIn(anyLong(), eq(c))).thenThrow(SQLException.class);

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.getMeetingsSpeakerIsInvolvedIn(228L));

        String expected = "Cannot get meetings in which speaker is involved";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnSetWithSpeakerOfMeetingByHisId() throws SQLException, DataBaseException {
        SpeakerService speakerService = mock(SpeakerService.class);
        meetingService.setSpeakerService(speakerService);
        when(speakerService.getSpeakerById(322L)).thenReturn(createSpeaker());
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getLong(RESULT_SET_SPEAKER_ID)).thenReturn(322L);

        when(c.prepareStatement(GET_ALL_SPEAKER_BY_MEETING_ID_SQL)).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);

        int expectedSize = 1;
        int actualSize = meetingService.getSpeakersByMeetingId(99999L).size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotGetSpeakerByMeetingId() throws SQLException {
        MeetingDao meetingDao = mock(MeetingDao.class);
        meetingService.setMeetingDao(meetingDao);
        when(meetingDao.getSpeakerIdsByMeetingId(anyLong(), eq(c))).thenThrow(SQLException.class);

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.getSpeakersByMeetingId(13L));

        String expected = "Cannot get speaker by meeting ID 13";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldMarkPresentUsers() throws SQLException, DataBaseException {
        when(c.prepareStatement(anyString())).thenReturn(p);

        String[] presentUsers = {"20", "22"};
        Long meetingId = 22L;

        meetingService.markPresentUsers(presentUsers, meetingId);

        verify(c, times(1)).commit();
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotMarkPresentUsers() throws SQLException {
        MeetingDao meetingDao = mock(MeetingDao.class);
        meetingService.setMeetingDao(meetingDao);

        String[] presentUsers = {"32", "2"};
        Long meetingId = 78L;

        doThrow(SQLException.class).when(meetingDao).markPresentUsers(presentUsers, meetingId, c);

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.markPresentUsers(presentUsers, meetingId));

        String expected = "Cannot mark present users";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnSetWithPresentUserIds() throws SQLException, DataBaseException {
        when(rs.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        when(rs.getLong("user_id"))
                .thenReturn(28L)
                .thenReturn(82L);

        when(c.prepareStatement(GET_PRESENT_USERS_SQL)).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);

        List<Long> expected = new LinkedList<>(Arrays.asList(28L, 82L));
        List<Long> actual = meetingService.getPresentUserIds(13L);

        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotGetPresentUsers() throws SQLException {
        MeetingDao meetingDao = mock(MeetingDao.class);
        meetingService.setMeetingDao(meetingDao);

        when(meetingDao.getPresentUserIds(anyLong(), eq(c))).thenThrow(SQLException.class);

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.getPresentUserIds(92L));

        String expected = "Cannot get present users";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    private Meeting createFirstMeeting() {
        Meeting meeting = new Meeting();
        meeting.setId(9L);
        meeting.setName(Util.generateStringWithRandomChars(16));
        meeting.setTimeStart("22:40");
        meeting.setTimeEnd("23:59");
        meeting.setDate("22.08.2022");
        meeting.setPlace("Kharkov");
        return meeting;
    }

    private Meeting createSecondMeeting() {
        Meeting newMeeting = createFirstMeeting();
        newMeeting.setTimeStart("11:40");
        newMeeting.setTimeEnd("15:59");
        newMeeting.setDate("23.08.2022");
        newMeeting.setPlace("Kyiv");
        return newMeeting;
    }
}