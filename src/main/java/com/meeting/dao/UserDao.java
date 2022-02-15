package com.meeting.dao;

import com.meeting.entitiy.Role;
import com.meeting.entitiy.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao extends Dao<User> {

    /**
     *
     * @param id
     * @return user's role by his ID
     */
    Role getUserRole(long id, Connection c) throws SQLException;

    /**
     * Updates user's role by his ID
     * @param id
     */
    void updateUserRole(Long id, Connection c) throws SQLException;

    Optional<User> getUserByLogin(String login, Connection c) throws SQLException;

    List<User> getAllUserByRole(String role, Connection c) throws SQLException;

    /**
     * puts user ID and meeting ID into table meeting_participants
     * @param userId
     * @param meetingId
     */
    void participate(Long userId, Long meetingId, Connection c) throws SQLException;

    /**
     * @return set with meeting IDs in which user takes part
     */
    List<Long> getMeetingIdsUserTakesPart(Long userId, Connection c) throws SQLException;

    /**
     * removes user ID and meeting ID into table meeting_participants
     * @param userId
     * @param meetingId
     */
    void stopParticipating(Long userId, Long meetingId, Connection c) throws SQLException;

    /**
     *
     * Sets email for user by his ID
     */
    void setEmail(Long userId, String email, Connection c) throws SQLException;
}
