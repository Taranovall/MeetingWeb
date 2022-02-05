package com.meeting.dao;

import com.meeting.entitiy.Speaker;
import com.meeting.exception.DataBaseException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SpeakerDao extends Dao<Speaker>{

    /**
     * Sends offer to this current user to be at the meeting as a speaker.
     * User must have role "Speaker".
     */
     boolean sendInvite(Long speakerId, Long topicId, Long userSessionId, Connection c) throws SQLException;

     boolean rollbackInvite(Long speakerId, Long topicId, Connection c) throws SQLException;

    Optional<Speaker> getSpeakerByTopicId(Long topicId, Connection c) throws SQLException, DataBaseException;

    List<String> getSentApplicationBySpeakerId(Long speakerId, Connection c) throws SQLException;

}
