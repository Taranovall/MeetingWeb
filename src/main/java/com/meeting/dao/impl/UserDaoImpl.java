package com.meeting.dao.impl;

import com.meeting.dao.UserDao;
import com.meeting.entitiy.Role;
import com.meeting.entitiy.User;
import com.meeting.util.SQLQuery;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.meeting.util.SQLQuery.*;

public class UserDaoImpl implements UserDao {

    @Override
    public Optional<User> getById(Long id, Connection c) throws SQLException {
        Optional<User> optionalUser = Optional.empty();
        PreparedStatement p = c.prepareStatement(GET_USER_BY_ID_SQL);
        p.setLong(1, id);
        ResultSet rs = p.executeQuery();
        if (rs.next()) {
            User user = extractUser(rs, c);
            optionalUser = Optional.of(user);
        }
        return optionalUser;
    }

    @Override
    public List<User> getAll(Connection c) throws SQLException {
        List<User> users = new LinkedList<>();
        PreparedStatement p = c.prepareStatement(GET_ALL_USERS_SQL);
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            User user = extractUser(rs, c);
            user.setRole(getUserRole(user.getId(), c));
            users.add(user);
        }
        return users;
    }

    @Override
    public Role getUserRole(long id, Connection c) throws SQLException {
        Role role = null;
        PreparedStatement p = c.prepareStatement(GET_USER_ROLE_BY_ID_SQL);
        p.setLong(1, id);
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            role = Role.getRoleByString(rs.getString("name"));
        }
        return role;
    }

    @Override
    public void save(User user, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(CREATE_USER_SQL, Statement.RETURN_GENERATED_KEYS);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();

        String login = user.getLogin();
        String password = user.getPassword();
        String registrationDate = dtf.format(now);

        p.setString(1, login);
        p.setString(2, password);
        p.setString(3, registrationDate);

        if (p.executeUpdate() > 0) {
            ResultSet rs = p.getGeneratedKeys();
            if (rs.next()) {
                Long usedId = rs.getLong(1);
                user.setRole(Role.USER);
                user.setId(usedId);
                updateUserRole(usedId, c);
            }
        }
    }

    @Override
    public Optional<User> getUserByLogin(String login, Connection c) throws SQLException {
        Optional<User> optionalUser = Optional.empty();
        PreparedStatement p = c.prepareStatement(GET_USER_BY_LOGIN_SQL);
        p.setString(1, login);
        ResultSet rs = p.executeQuery();
        if (rs.next()) {
            User user = extractUser(rs, c);
            optionalUser = Optional.of(user);
        }
        return optionalUser;
    }

    @Override
    public List<User> getAllUserByRole(String role, Connection c) throws SQLException {
        List<User> users = new LinkedList<>();
        PreparedStatement p = c.prepareStatement(GET_ALL_USERS_BY_ROLE_SQL);
        p.setString(1, role);
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            users.add(extractUser(rs, c));
        }
        return users;
    }

    @Override
    public void participate(Long userId, Long meetingId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(SQLQuery.USER_PARTICIPATE_SQL);
        p.setLong(1, meetingId);
        p.setLong(2, userId);
        p.executeUpdate();
    }

    @Override
    public List<Long> getMeetingIdsUserTakesPart(Long userId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(SQLQuery.GET_MEETING_IDS_IN_WHICH_USER_TAKES_PARK_SQL);
        p.setLong(1, userId);
        List<Long> userMeetingIds = new LinkedList<>();
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            Long meetingId = rs.getLong(1);
            userMeetingIds.add(meetingId);
        }
        return userMeetingIds;
    }

    @Override
    public void stopParticipating(Long userId, Long meetingId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(SQLQuery.USER_STOP_PARTICIPATING_SQL);
        p.setLong(1, meetingId);
        p.setLong(2, userId);
        p.executeUpdate();
    }

    @Override
    public void setEmail(Long userId, String email, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(SQLQuery.SET_EMAIL_FOR_USER_SQL);
        p.setString(1, email);
        p.setLong(2, userId);
        p.executeUpdate();
    }

    @Override
    public void delete(User user, Connection c) {

    }

    @Override
    public void updateUserRole(Long id, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(ADD_ROLE_FOR_USER_SQL);
        p.setLong(1, id);
        p.setString(2, Role.USER.name());
        p.executeUpdate();
    }

    private User extractUser(ResultSet rs, Connection c) throws SQLException {
        Long id = rs.getLong("id");
        String login = rs.getString("login");
        String password = rs.getString("password");
        String registrationDate = rs.getString("registration_date");
        String email = rs.getString("email");
        User user = new User(id, login, password);
        user.setRegistrationDate(registrationDate);
        user.setRole(getUserRole(user.getId(), c));
        user.setMeetingIdsSetUserTakesPart(getMeetingIdsUserTakesPart(user.getId(), c));
        if (!rs.wasNull()) user.setEmail(email);
        return user;
    }
}
