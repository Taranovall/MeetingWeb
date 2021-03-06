package com.meeting.dao;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Topic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public interface TopicDao extends Dao<Topic> {

    /**
     * @param meetingId
     * @param topicName
     * @return true if topics with name <code>topicName</code> in meeting with id <code>meetingId</code> exists
     */
    boolean isTopicExist(String topicName, Long meetingId, Connection c) throws SQLException;

    /**
     * Creates topic in database and links with meeting
     */
    void addTopicsToMeeting(Long meetingId, Set<Topic> topics, Connection c) throws SQLException;

    /**
     * Creates topic without speaker
     */
    void createFreeTopic(Meeting meeting, Topic topic, Connection c) throws SQLException;

    void save(Topic topic, Connection c) throws SQLException;

   /**
     *
     * @return set with all topics without speaker
     */
    Set<Topic> getAllFreeTopicsByMeetingId(Long meetingId, Connection c) throws SQLException;
}
