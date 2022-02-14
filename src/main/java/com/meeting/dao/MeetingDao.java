package com.meeting.dao;

import com.meeting.entitiy.Meeting;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MeetingDao extends Dao<Meeting> {

    /**
     * @return set with meeting's id in which speaker are involved in by his ID
     */
    Set<Long> getAllMeetingsIdSpeakerInvolvesIn(Long speakerId, Connection c) throws SQLException;

    /**
     * @param meetingId
     * @param c
     * @return Speaker IDs and Topic IDs in which speakers has agreed to take part
     */
    Map<Long, Long> getTopicSpeakerIdMapByMeetingId(Long meetingId, Connection c) throws SQLException;

    /**
     * @param meetingId
     * @param c
     * @return map with Topic IDs and Set with Speaker ID IDs which sent application to this topic
     */
    Map<Long, Set<Long>> getSentApplicationsByMeetingId(Long meetingId, Connection c) throws SQLException;


    /**
     * Function for speaker to propose his own topic for particular meeting
     * @param meetingId
     * @param userSessionId
     * @param topicId
     * @return true if the function was successful
     */
    boolean proposeTopic(Long meetingId, Long userSessionId, Long topicId, Connection c) throws SQLException;

    /**
     * Function for moderator to accept proposed topic by speaker
     * @param topicId
     * @param speakerId
     * @param meetingId
     * @return true if the function was successful
     */
    boolean acceptProposedTopics(Long topicId, Long speakerId, Long meetingId, Connection c) throws SQLException;

    /**
     * @param meetingId
     * @return proposed topics by speaker as a map where Topic ID is a key and Speaker ID is a value
     */
    Map<Long, Long> getProposedTopicsBySpeakerByMeetingId(Long meetingId, Connection c) throws SQLException;

    /**
     * Function for moderator to cancel proposed topic by speaker
     * @param topicId
     * @param speakerId
     * @return true if the function was successful
     */
    boolean cancelProposedTopic(Long topicId, Long speakerId, Connection c) throws SQLException;


    /**
     * @param meeting
     * Updates meeting's fields in table 'meetings'
     */
    void updateInformation(Meeting meeting, Connection c) throws SQLException;

    /**
     *
     * @return all users' id which are participant of the meeting
     */
    List<Long> getParticipantsIdByMeetingId(Long meetingId, Connection c) throws SQLException;

    /**
     *
     * @param presentUsers array with users which present on the meeting
     * @param meetingId
     */
    void markPresentUsers(String[] presentUsers, Long meetingId, Connection c) throws SQLException;

    /**
     *
     * @return a list with the IDs of the users who are present on the meeting
     */
    List<Long> getPresentUserIds(Long meetingId, Connection c) throws SQLException;

    /**
     *
     * @return percentage of users' attendance
     */
    double getAttendancePercentageByMeetingId(Long meetingId, Connection c) throws SQLException;
}
