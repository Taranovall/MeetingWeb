package com.meeting.dao;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Topic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public interface TopicDao extends Dao<Topic> {

    /**
     * Creates topic in database and links with meeting
     */
    void addTopicsToMeeting(Long meetingId, Set<Topic> topics, Connection c) throws SQLException;

    /**
     * Creates topic without speaker
     */
    void createFreeTopic(Meeting meeting, Topic topic, Connection c) throws SQLException;


    /**
     * @return all topics that have been added to this meeting
     */
    Set<Topic> getTopicsByMeetingId(Long meetingId, Connection c) throws SQLException;

    Set<Topic> getAllFreeTopicsByMeetingId(Long meetingId, Connection c) throws SQLException;
}
