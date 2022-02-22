package com.meeting.service.impl;

import com.meeting.dao.TopicDao;
import com.meeting.dao.impl.TopicDaoImpl;
import com.meeting.entitiy.Topic;
import com.meeting.exception.DataBaseException;
import com.meeting.service.connection.ConnectionPool;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import util.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.meeting.util.SQLQuery.GET_ALL_FREE_TOPICS_BY_MEETING_ID;
import static com.meeting.util.SQLQuery.GET_TOPIC_BY_ID_SQL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TopicServiceImplTest {

    @Mock
    private ResultSet rs;
    @Mock
    private Connection c;
    @Mock
    private AutoCloseable closeable;
    private PreparedStatement p;
    private TopicServiceImpl topicService;

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
        topicService = new TopicServiceImpl();
        p = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);

    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        c = null;
        p = null;
        topicService = null;
    }

    @AfterAll
    static void afterAll() {
        connectionPool = null;
        mocked.close();
    }

    @Test
    void shouldReturnTopicByHisId() throws SQLException, DataBaseException {
        Long topicId = 22L;
        String topicName = "Why do i have to make up topic's name?";
        when(c.prepareStatement(GET_TOPIC_BY_ID_SQL)).thenReturn(p);
        when(rs.next())
                .thenReturn(true)
                .thenReturn(false);
        when(rs.getLong("id")).thenReturn(topicId);
        when(rs.getString("name")).thenReturn(topicName);
        when(p.executeQuery()).thenReturn(rs);

        Topic expectedTopic = new Topic(topicId, topicName);
        Topic actualTopic = topicService.getById(topicId);

        assertEquals(expectedTopic, actualTopic);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage() throws SQLException {
        when(c.prepareStatement(GET_TOPIC_BY_ID_SQL)).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);
        DataBaseException thrown = assertThrows(DataBaseException.class, () -> topicService.getById(2L));

        String expected = "Cannot get topic by his ID: 2";
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void shouldReturnAllFreeTopicsByMeetingId() throws SQLException, DataBaseException {
        TopicDao topicDao = mock(TopicDaoImpl.class);
        topicService.setTopicDao(topicDao);
        List<Topic> topicList = new ArrayList<>();
        topicList.add(new Topic(1L, Utils.generateStringWithRandomChars(7)));
        topicList.add(new Topic(2L, Utils.generateStringWithRandomChars(15)));
        topicList.add(new Topic(3L, Utils.generateStringWithRandomChars(19)));

        when(rs.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(topicDao.getById(anyLong(), eq(c)))
                .thenReturn(Optional.of(topicList.get(0)))
                .thenReturn(Optional.of(topicList.get(1)))
                .thenReturn(Optional.of(topicList.get(2)));

        when(c.prepareStatement(GET_ALL_FREE_TOPICS_BY_MEETING_ID)).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);
        when(topicDao.getAllFreeTopicsByMeetingId(anyLong(), eq(c))).thenCallRealMethod();


        Set<Topic> expectedSet = new HashSet<>(topicList);
        Set<Topic> actualSet = topicService.getAllFreeTopicsByMeetingId(4L);

        assertEquals(expectedSet, actualSet);
    }

    @Test
    void shouldReturnDataBaseExceptionWithMessage() throws SQLException {
        TopicDao topicDao = mock(TopicDaoImpl.class);
        topicService.setTopicDao(topicDao);
        doThrow(SQLException.class).when(topicDao).getAllFreeTopicsByMeetingId(anyLong(), eq(c));

        DataBaseException thrown = assertThrows(DataBaseException.class, () -> topicService.getAllFreeTopicsByMeetingId(3L));

        String exceptedMessage = "Cannot get all free topics by meeting ID: 3";
        String actualMessage = thrown.getMessage();

        assertEquals(exceptedMessage, actualMessage);
    }
}