package com.meeting.service.impl;

import com.meeting.dao.MeetingDao;
import com.meeting.dao.SpeakerDao;
import com.meeting.dao.TopicDao;
import com.meeting.dao.UserDao;
import com.meeting.dao.impl.SpeakerDaoImpl;
import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Role;
import com.meeting.entitiy.Speaker;
import com.meeting.entitiy.Topic;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.connection.ConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.meeting.util.SQLQuery.GET_ALL_USERS_BY_ROLE_SQL;
import static com.meeting.util.SQLQuery.GET_MEETING_IDS_IN_WHICH_USER_TAKES_PART_SQL;
import static com.meeting.util.SQLQuery.GET_RECEIVED_APPLICATIONS_BY_SPEAKER_ID_SQL;
import static com.meeting.util.SQLQuery.GET_SENT_APPLICATIONS_BY_SPEAKER_ID_SQL;
import static com.meeting.util.SQLQuery.GET_SPEAKER_RESPONSE_TO_THE_OFFER;
import static com.meeting.util.SQLQuery.GET_USER_ROLE_BY_ID_SQL;
import static com.meeting.util.SQLQuery.INVITE_SPEAKER_TO_MEETING_SQL;
import static com.meeting.util.SQLQuery.ROLLBACK_INVITE_SQL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class SpeakerServiceImplTest {

    private static final String ID = "id";
    private static final String LOGIN = "login";
    private static final String NAME = "name";
    private static final String SPEAKER = "Speaker";
    private static final String TOPIC_ID = "topic_id";
    private static final String MEETING_ID = "meeting_id";
    private static final String INVITATION = "invitation";
    @Mock
    private ResultSet rs;
    @Mock
    private Connection c;
    @Mock
    private AutoCloseable closeable;
    private PreparedStatement p;
    private SpeakerServiceImpl speakerService;

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
        speakerService = new SpeakerServiceImpl();
        p = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);

    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        c = null;
        p = null;
        speakerService = null;
    }

    @AfterAll
    static void afterAll() {
        connectionPool = null;
        mocked.close();
    }

    @Test
    void shouldReturnListWithAllSpeakers() throws SQLException, DataBaseException {
        List<Speaker> speakerList = new ArrayList<>();
        speakerList.add(new Speaker(1L, Util.generateStringWithRandomChars(10)));
        speakerList.add(new Speaker(2L, Util.generateStringWithRandomChars(10)));
        speakerList.add(new Speaker(3L, Util.generateStringWithRandomChars(10)));

        when(rs.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(rs.getLong(ID))
                .thenReturn(speakerList.get(0).getId())
                .thenReturn(speakerList.get(1).getId())
                .thenReturn(speakerList.get(2).getId());
        when(rs.getString(LOGIN))
                .thenReturn(speakerList.get(0).getLogin())
                .thenReturn(speakerList.get(1).getLogin())
                .thenReturn(speakerList.get(2).getLogin());

        when(c.prepareStatement(GET_ALL_USERS_BY_ROLE_SQL)).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);
        when(rs.getString(NAME)).thenReturn(SPEAKER);

        when(c.prepareStatement(GET_USER_ROLE_BY_ID_SQL)).thenReturn(p);
        when(c.prepareStatement(GET_MEETING_IDS_IN_WHICH_USER_TAKES_PART_SQL)).thenReturn(p);

        List<Speaker> actualList = speakerService.getAllSpeakers();

        assertEquals(speakerList, actualList);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage() throws SQLException, DataBaseException {
        doThrow(SQLException.class).when(c).prepareStatement(GET_ALL_USERS_BY_ROLE_SQL);
        DataBaseException thrown = assertThrows(DataBaseException.class, () -> speakerService.getAllSpeakers());

        String expected = "Cannot get all speakers";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnSpeakerById() throws SQLException, DataBaseException {
        SpeakerDaoImpl speakerDao = new SpeakerDaoImpl();
        UserDao userDao = mock(UserDao.class);
        TopicDao topicDao = mock(TopicDao.class);
        MeetingDao meetingDao = mock(MeetingDao.class);
        speakerDao.setTopicDao(topicDao);
        speakerDao.setUserDao(userDao);
        speakerDao.setMeetingDao(meetingDao);
        speakerService.setSpeakerDao(speakerDao);
        when(userDao.getById(anyLong(), eq(c))).thenReturn(Optional.of(new User(1L, "TheUserMMM")));
        when(userDao.getUserRole(anyLong(), eq(c))).thenReturn(Role.SPEAKER);

        when(topicDao.getById(3L, c)).thenReturn(Optional.of(new Topic(3L, Util.generateStringWithRandomChars(9))));
        when(topicDao.getById(6L, c)).thenReturn(Optional.of(new Topic(3L, Util.generateStringWithRandomChars(15))));

        when(meetingDao.getById(7L, c)).thenReturn(Optional.of(new Meeting()));
        when(meetingDao.getById(9L, c)).thenReturn(Optional.of(new Meeting()));

        when(rs.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        when(rs.getLong(TOPIC_ID))
                .thenReturn(3L)
                .thenReturn(6L);
        when(rs.getLong(MEETING_ID))
                .thenReturn(7L)
                .thenReturn(9L);
        when(rs.getBoolean(INVITATION))
                .thenReturn(true)
                .thenReturn(false);

        when(c.prepareStatement(GET_SPEAKER_RESPONSE_TO_THE_OFFER)).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);

        Speaker speaker = speakerService.getSpeakerById(1L);
        assertNotNull(speaker);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotGetSpeakerById() throws SQLException {
        SpeakerDao speakerDao = mock(SpeakerDao.class);
        speakerService.setSpeakerDao(speakerDao);

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> speakerService.getSpeakerById(7L));

        String expected = "Cannot get speaker by ID: 7";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnSentApplications() throws SQLException, DataBaseException {
        when(rs.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(rs.getLong(TOPIC_ID))
                .thenReturn(3L)
                .thenReturn(4L);
        when(c.prepareStatement(GET_SENT_APPLICATIONS_BY_SPEAKER_ID_SQL)).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);

        List<String> expected = new ArrayList<>(Arrays.asList("3", "4"));
        List<String> actual = speakerService.getSentApplications(4L);

        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotGetSentApplicationBySpeakerId() throws SQLException {
        SpeakerDao speakerDao = mock(SpeakerDao.class);
        speakerService.setSpeakerDao(speakerDao);
        when(speakerDao.getApplicationBySpeakerId(anyLong(), eq(GET_SENT_APPLICATIONS_BY_SPEAKER_ID_SQL), eq(c))).thenThrow(SQLException.class);

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> speakerService.getSentApplications(4L));

        String expected = "Cannot get sent applications by speaker by his ID: 4";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldAcceptApplicationAndReturnTrue() throws SQLException, DataBaseException {
        when(c.prepareStatement(anyString())).thenReturn(p);
        boolean result = speakerService.acceptApplication(13L, 37L);
        assertTrue(result);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotAcceptApplication() throws SQLException {
        SpeakerDao speakerDao = mock(SpeakerDao.class);
        speakerService.setSpeakerDao(speakerDao);
        when(speakerDao.acceptApplication(anyLong(), anyLong(), eq(c))).thenThrow(SQLException.class);

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> speakerService.acceptApplication(4L,8L));

        String expected = "Cannot accept application";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnListWithReceivedApplications() throws SQLException, DataBaseException {
        SpeakerDao speakerDao = mock(SpeakerDao.class);
        speakerService.setSpeakerDao(speakerDao);
        when(speakerDao.getApplicationBySpeakerId(anyLong(), eq(GET_RECEIVED_APPLICATIONS_BY_SPEAKER_ID_SQL), eq(c)))
                .thenReturn(new LinkedList<>(Arrays.asList("4","892")));

        int expectedSize = 2;
        int actualSize = speakerService.getReceivedApplications(6L).size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotGetReceivedBySpeakerApplicationsByHisId() throws SQLException {
        SpeakerDao speakerDao = mock(SpeakerDao.class);
        speakerService.setSpeakerDao(speakerDao);
        when(speakerDao.getApplicationBySpeakerId(anyLong(),eq(GET_RECEIVED_APPLICATIONS_BY_SPEAKER_ID_SQL), eq(c))).thenThrow(SQLException.class);

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> speakerService.getReceivedApplications(4L));

        String expected = "Cannot get received by speaker applications by his ID: 4";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldAcceptInvitationAndReturnTrue() throws SQLException, DataBaseException {
        SpeakerDao speakerDao = mock(SpeakerDao.class);
        speakerService.setSpeakerDao(speakerDao);
        when(speakerDao.acceptApplication(anyLong(), anyLong(), eq(c))).thenReturn(true);
        boolean result = speakerService.acceptInvitation(13L, 37L);
        assertTrue(result);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotAcceptInvitation() throws SQLException {
        SpeakerDao speakerDao = mock(SpeakerDao.class);
        speakerService.setSpeakerDao(speakerDao);
        when(speakerDao.acceptApplication(anyLong(), anyLong(), eq(c))).thenThrow(SQLException.class);

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> speakerService.acceptInvitation(4L,8L));

        String expected = "Cannot accept invitation";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldCancelInvitationAndReturnTrue() throws SQLException, DataBaseException {
        when(c.prepareStatement(ROLLBACK_INVITE_SQL)).thenReturn(p);

        boolean actual = speakerService.cancelInvitation(4L, 99L);
        assertTrue(actual);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotCancelInvitation() throws SQLException {
        SpeakerDao speakerDao = mock(SpeakerDao.class);
        speakerService.setSpeakerDao(speakerDao);
        when(speakerDao.rollbackInvite(anyLong(), anyLong(), eq(c))).thenThrow(SQLException.class);

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> speakerService.cancelInvitation(4L,8L));

        String expected = "Cannot cancel invitation";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnTrueAndSendApplication() throws SQLException, DataBaseException {
        when(c.prepareStatement(INVITE_SPEAKER_TO_MEETING_SQL)).thenReturn(p);

        boolean result = speakerService.sendApplication(28L, 92L);
        assertTrue(result);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotSendApplication() throws SQLException {
        SpeakerDao speakerDao = mock(SpeakerDao.class);
        speakerService.setSpeakerDao(speakerDao);
        when(speakerDao.sendInvite(anyLong(), anyLong(), anyLong(), eq(c))).thenThrow(SQLException.class);

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> speakerService.sendApplication(4L,28L));

        String expected = "Cannot send application";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnTrueAndRemoveApplication() throws DataBaseException {
        SpeakerDao speakerDao = mock(SpeakerDao.class);
        speakerService.setSpeakerDao(speakerDao);

        boolean result = speakerService.removeApplication(28L, 92L);
        assertTrue(result);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage_CannotRemoveApplication() throws SQLException {
        SpeakerDao speakerDao = mock(SpeakerDao.class);
        speakerService.setSpeakerDao(speakerDao);
        when(speakerDao.rollbackInvite(anyLong(), anyLong(), eq(c))).thenThrow(SQLException.class);

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> speakerService.removeApplication(4L,28L));

        String expected = "Cannot remove application";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }
}