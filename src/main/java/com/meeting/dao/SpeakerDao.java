package com.meeting.dao;

import com.meeting.entitiy.Speaker;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface SpeakerDao extends Dao<Speaker> {

    /**
     * Sends offer to user with id {@code speakerId} to be at the meeting as a speaker.
     *
     * @param speakerId     user ID which get invited
     * @param userSessionId user ID which sends invite to a particular speaker
     * @param topicId       topic ID to which speaker is invited
     * @return true if the function was successful
     */
    boolean sendInvite(Long speakerId, Long topicId, Long userSessionId, Connection c) throws SQLException;

    /**
     * Rollbacks sent invite
     *
     * @return true if the function was successful
     */
    boolean rollbackInvite(Long speakerId, Long topicId, Connection c) throws SQLException;

    List<Speaker> getAll(Connection c) throws SQLException;

    /**
     * @param speakerId
     * @param SQLQuery  query on which the result depends
     * @return returns all speaker's applications by his ID
     */
    List<String> getApplicationBySpeakerId(Long speakerId, String SQLQuery, Connection c) throws SQLException;

    /**
     * Accept application
     *
     * @param speakerId
     * @param topicId
     * @return true if the function was successful
     */
    boolean acceptApplication(Long speakerId, Long topicId, Connection c) throws SQLException;
}
