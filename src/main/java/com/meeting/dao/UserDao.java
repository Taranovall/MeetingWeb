package com.meeting.dao;

import com.meeting.entitiy.Role;
import com.meeting.entitiy.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao extends Dao<User> {

    Role getUserRole(long id, Connection c) throws SQLException;

    void addRoleForUser(Long id, Connection c) throws SQLException;

    Optional<User> getUserByLogin(String login, Connection c) throws SQLException;

    List<User> getAllUserByRole(String role, Connection c) throws SQLException;

    void participate(Long userId, Long meetingId, Connection c) throws SQLException;

    /**
     * @return set with meeting IDs in which user takes part
     */
    List<Long> getMeetingIdsUserTakesPart(Long userId, Connection c) throws SQLException;

    void stopParticipating(Long userId, Long meetingId, Connection c) throws SQLException;
}
