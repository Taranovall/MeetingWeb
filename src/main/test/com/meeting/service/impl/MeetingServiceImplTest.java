package com.meeting.service.impl;

import com.meeting.dao.MeetingDao;
import com.meeting.dao.TopicDao;
import com.meeting.dao.impl.MeetingDaoImpl;
import com.meeting.entitiy.*;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.EmailException;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.SpeakerService;
import com.meeting.service.TopicService;
import com.meeting.service.UserService;
import com.meeting.service.connection.ConnectionPool;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import util.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static com.meeting.util.SQLQuery.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeetingServiceImplTest {

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
    void shouldCreateMeetingAndMakeCommit() throws IOException, DataBaseException, SQLException, UserNotFoundException {
        UserService userService = mock(UserService.class);
        meetingService.setUserService(userService);
        when(userService.getUserByLogin("SpeakerWithoutName")).thenReturn(new User(7L, "SpeakerWithoutName"));
        User user = createUser();
        Meeting meeting = createFirstMeeting();
        String[] topics = {"Topic1", "Topic2"};
        String[] speakers = {"SpeakerWithoutName", "none"};

        File image = new File("img.jpeg");

        when(rs.next()).thenReturn(true);
        when(c.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(p);
        when(c.prepareStatement(anyString())).thenReturn(p);
        when(p.executeUpdate()).thenReturn(1);
        when(p.getGeneratedKeys()).thenReturn(rs);

        meetingService.createMeeting(user, meeting, topics, speakers, image);

        verify(c, times(1)).commit();
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotCreateMeeting() throws SQLException, DataBaseException, UserNotFoundException {
        User user = createUser();
        Meeting meeting = createFirstMeeting();
        String[] topics = {"Topic1", "Topic2"};
        String[] speakers = {"SpeakerWithoutName", "none"};

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        TopicDao topicDao = mock(TopicDao.class);
        UserService userService = mock(UserService.class);
        MeetingDao meetingDao = mock(MeetingDao.class);

        when(userService.getUserByLogin("SpeakerWithoutName")).thenReturn(new User(7L, "SpeakerWithoutName"));
        when(req.getSession()).thenReturn(session);
        when(session.getAttribute("language")).thenReturn("en");
        doThrow(SQLException.class).when(topicDao).addTopicsToMeeting(anyLong(), any(), eq(c));

        meetingService.setTopicDao(topicDao);
        meetingService.setUserService(userService);
        meetingService.setMeetingDao(meetingDao);

        File image = new File("img.png");
        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.createMeeting(user, meeting, topics, speakers, image));

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
        when(rs.getLong("id")).thenReturn(2L);
        when(rs.getString("name")).thenReturn("InvestPro Кипр Никосия 2022");
        when(rs.getString("date")).thenReturn("2022-06-20");
        when(rs.getString("time_start")).thenReturn("15:30");
        when(rs.getString("time_end")).thenReturn("16:30");
        when(rs.getString("place")).thenReturn("Hilton Nicosia");
        when(rs.getString("photo_path")).thenReturn("/image/123qq.jpeg");
        when(rs.getLong("speaker_id")).thenReturn(7L);
        when(rs.getLong("topic_id")).thenReturn(9L);
        when(c.prepareStatement(anyString())).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);

        meetingService.setTopicService(topicService);
        meetingService.setSpeakerService(speakerService);
        meetingService.setMeetingDao(meetingDao);
        when(meetingDao.getById(anyLong(), eq(c))).thenCallRealMethod();
        when(topicService.getAllFreeTopicsByMeetingId(2L)).thenReturn(new HashSet<>(Collections.singletonList(new Topic(1L, Utils.generateStringWithRandomChars(10)))));
        when(speakerService.getSpeakerById(37L)).thenReturn(new Speaker(7L, "Speaker"));
        when(topicService.getById(anyLong()))
                .thenReturn(new Topic(9L, Utils.generateStringWithRandomChars(9)))
                .thenReturn(new Topic(13L, Utils.generateStringWithRandomChars(9)));

        when(meetingDao.getSentApplicationsByMeetingId(2L, c)).thenCallRealMethod();
        when(rs.getLong("topic_id")).thenReturn(25L);
        when(rs.getLong("speaker_id"))
                .thenReturn(8L)
                .thenReturn(15L);
        //Map<Long, Set<Long>> applicationMap = new HashMap<>();
        // applicationMap.put(25L, new HashSet<>(Arrays.asList(8L, 15L)));
        //when(meetingDao.getSentApplicationsByMeetingId(2L, c)).thenReturn(applicationMap);

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

        String expected = "Meeting with ID 4 doesn't exist";
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

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.proposeTopic(4L, 10L, Utils.generateStringWithRandomChars(15)));

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
        when(c.prepareStatement(REMOVE_PROPOSED_TOPIC_SQL)).thenReturn(p);

        boolean result = meetingService.cancelProposedTopic(13L, 37L);
        assertTrue(result);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotCancelProposedTopic() throws SQLException {
        MeetingDao meetingDao = mock(MeetingDao.class);
        meetingService.setMeetingDao(meetingDao);
        doThrow(SQLException.class).when(meetingDao).cancelProposedTopic(anyLong(), anyLong(), any());

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> meetingService.cancelProposedTopic(4L, 10L));

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

        when(rs.getLong("id")).thenReturn(2L).thenReturn(7L);
        when(rs.getString("name")).thenReturn("InvestPro Кипр Никосия 2022").thenReturn("Invest Pro Украина Киев 2022");
        when(rs.getString("date")).thenReturn("2022-06-20").thenReturn("2022-09-20");
        when(rs.getString("time_start")).thenReturn("15:30").thenReturn("19:40");
        when(rs.getString("time_end")).thenReturn("16:30").thenReturn("22:31");
        when(rs.getString("place")).thenReturn("Hilton Nicosia").thenReturn("Hilton Kyiv");
        when(rs.getString("photo_path")).thenReturn("/image/123qq.jpeg").thenReturn("/image/123Kyiv.jpeg");

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

        when(rs.getLong("speaker_id"))
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
        when(rs.getLong("speaker_id")).thenReturn(322L);

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
        meeting.setName(Utils.generateStringWithRandomChars(16));
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

    private User createUser() {
        User user = new User();
        user.setId(7L);
        user.setLogin("TempUSer");
        user.setPassword("aksdlaskldas33");
        user.setRegistrationDate("24.08.2021");
        user.setEmail("mailTest-t@gmail.com");
        user.setRole(Role.USER);
        return user;
    }

    private Speaker createSpeaker() {
        return new Speaker(322L, "not talkative speaker");
    }
}