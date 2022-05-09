package com.meeting.service.impl;

import com.meeting.dao.UserDao;
import com.meeting.dao.impl.UserDaoImpl;
import com.meeting.entitiy.Role;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.UserNotFoundException;
import com.meeting.connection.ConnectionPool;
import com.meeting.util.SQLQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import static com.meeting.util.SQLQuery.ADD_ROLE_FOR_USER_SQL;
import static com.meeting.util.SQLQuery.CREATE_USER_SQL;
import static com.meeting.util.SQLQuery.USER_PARTICIPATE_SQL;
import static com.meeting.util.SQLQuery.USER_STOP_PARTICIPATING_SQL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Constant.USER;

class UserServiceImplTest {

    private static final String ID = "id";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String REGISTRATION_DATE = "registration_date";
    private static final String EMAIL = "email";
    private static final String NAME = "name";
    private static final String SPEAKER_LOGIN = "Speaker827";
    private static final String SPEAKER_PASSWORD = "dwerd8299DD";
    private static final String SPEKAER_REGISTRATION_DATE = "24.08.2021";
    private static final String SPEAKER_MAIL = "dabadabda@gmail.com";
    @Mock
    private ResultSet rs;
    @Mock
    private Connection c;
    @Mock
    private AutoCloseable closeable;
    private UserServiceImpl userService;
    private User user;
    private PreparedStatement p;

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
        userService = new UserServiceImpl();
        user = new User(SPEAKER_LOGIN, SPEAKER_PASSWORD);
        p = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);

    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        c = null;
        p = null;
        user = null;
        userService = null;
    }

    @AfterAll
    static void afterAll() {
        connectionPool = null;
        mocked.close();
    }

    @Test
    void shouldSignUpUser() throws SQLException, DataBaseException {
        when(c.prepareStatement(CREATE_USER_SQL, Statement.RETURN_GENERATED_KEYS)).thenReturn(p);
        when(p.executeUpdate()).thenReturn(1);
        when(p.getGeneratedKeys()).thenReturn(rs);

        when(rs.next()).thenReturn(true).thenReturn(false);

        when(rs.getLong(1)).thenReturn(1L);

        when(c.prepareStatement(ADD_ROLE_FOR_USER_SQL)).thenReturn(p);

        userService.signUpUser(user);
        verify(c, times(1)).commit();
    }

    @Test
    void shouldThrowDataBaseException() throws SQLException {
        when(c.prepareStatement(CREATE_USER_SQL, Statement.RETURN_GENERATED_KEYS)).thenReturn(p);
        doThrow(SQLException.class).when(p).setString(anyInt(), anyString());
        assertThrows(DataBaseException.class, () ->
                userService.signUpUser(user)
        );
    }

    @Test
    void shouldReturnUserByLogin() throws SQLException, UserNotFoundException {
        UserDao userDao = mock(UserDaoImpl.class);
        userService.setUserDao(userDao);
        when(rs.next()).thenReturn(true);
        when(c.prepareStatement(anyString())).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);

        when(rs.getLong(ID)).thenReturn(1L);
        when(rs.getString(LOGIN)).thenReturn(SPEAKER_LOGIN);
        when(rs.getString(PASSWORD)).thenReturn(SPEAKER_PASSWORD);
        when(rs.getString(REGISTRATION_DATE)).thenReturn(SPEKAER_REGISTRATION_DATE);
        when(rs.getString(EMAIL)).thenReturn(SPEAKER_MAIL);
        when(rs.getString(NAME)).thenReturn(USER);
        when(userDao.getUserByLogin(anyString(), eq(c))).thenCallRealMethod();
        when(userDao.getUserRole(anyLong(), eq(c))).thenReturn(Role.USER);
        when(userDao.getMeetingIdsUserTakesPart(anyLong(), eq(c))).thenReturn(new LinkedList<>());

        User user = userService.getUserByLogin(SPEAKER_LOGIN);

        assertNotNull(user);
    }

    @Test
    void shouldThrowExceptionWithMessageCannotGetUserByLogin() throws SQLException {
        when(c.prepareStatement(anyString())).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);
        assertThrows(UserNotFoundException.class, () ->
                userService.getUserByLogin("user1387")
        );
    }

    @Test
    void shouldReturnUserById() throws SQLException, UserNotFoundException {
        UserDao userDao = mock(UserDaoImpl.class);
        userService.setUserDao(userDao);
        when(rs.next()).thenReturn(true);
        when(c.prepareStatement(anyString())).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);

        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString(LOGIN)).thenReturn(SPEAKER_LOGIN);
        when(rs.getString(PASSWORD)).thenReturn(SPEAKER_PASSWORD);
        when(rs.getString(REGISTRATION_DATE)).thenReturn(SPEKAER_REGISTRATION_DATE);
        when(rs.getString(EMAIL)).thenReturn(SPEAKER_MAIL);
        when(rs.getString(NAME)).thenReturn(USER);
        when(userDao.getById(anyLong(), eq(c))).thenCallRealMethod();
        when(userDao.getUserRole(anyLong(), eq(c))).thenReturn(Role.USER);
        when(userDao.getMeetingIdsUserTakesPart(anyLong(), eq(c))).thenReturn(new LinkedList<>());

        User user = userService.getUserById(1L);

        assertNotNull(user);
    }

    @Test
    void shouldThrowExceptionWithMessageCannotGetUserById() throws SQLException {
        when(c.prepareStatement(anyString())).thenReturn(p);
        when(p.executeQuery()).thenReturn(rs);
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () ->
                userService.getUserById(1L)
        );

        assertEquals("Cannot get user by ID: 1", thrown.getMessage());
    }

    @Test
    void shouldCommitParticipating() throws SQLException, DataBaseException {
        when(c.prepareStatement(USER_PARTICIPATE_SQL)).thenReturn(p);
        userService.participate(13L, 37L);
        verify(c, times(1)).commit();
    }

    @Test
    void shouldReturnExceptionWithMessageUserCannotParticipate() throws SQLException {
        when(c.prepareStatement(USER_PARTICIPATE_SQL)).thenReturn(p);
        doThrow(SQLException.class).when(p).setLong(2, 7L);
        DataBaseException thrown = assertThrows(DataBaseException.class, () ->
                userService.participate(7L, 133L)
        );

        assertEquals("User cannot participate", thrown.getMessage());
    }

    @Test
    void shouldCommitStopParticipating() throws SQLException, DataBaseException {
        when(c.prepareStatement(USER_STOP_PARTICIPATING_SQL)).thenReturn(p);
        userService.stopParticipating(12L, 97L);
        verify(c, times(1)).commit();
    }

    @Test
    void shouldReturnExceptionWithMessageUserCannotStopParticipating() throws SQLException {
        when(c.prepareStatement(USER_STOP_PARTICIPATING_SQL)).thenReturn(p);
        doThrow(SQLException.class).when(p).setLong(2, 10L);
        DataBaseException thrown = assertThrows(DataBaseException.class, () ->
                userService.stopParticipating(10L, 20L)
        );

        assertEquals("User cannot stop participating", thrown.getMessage());
    }

    @Test
    void shouldMakeSQLQueryAndReturnTrue() throws SQLException, DataBaseException {
        when(c.prepareStatement(SQLQuery.SET_EMAIL_FOR_USER_SQL)).thenReturn(p);
        boolean result = userService.setEmail(1L, "email@gmail.com");
        assertTrue(result);
    }

    @Test
    void shouldThrowDataBaseExceptionWithMessage() throws SQLException {
        String email = "testMail@gmail.com";
        when(c.prepareStatement(SQLQuery.SET_EMAIL_FOR_USER_SQL)).thenReturn(p);
        doThrow(SQLException.class).when(p).setString(1, email);
        DataBaseException thrown = assertThrows(DataBaseException.class, () -> userService.setEmail(1L,email));

        String expected = String.format("Cannot set email '%s' for user with ID %s", email, 1);
        String actual = thrown.getMessage();
        assertEquals(expected, actual);
    }
}