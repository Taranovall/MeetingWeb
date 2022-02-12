package com.meeting.dao;

import com.meeting.entitiy.Meeting;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MeetingDao extends Dao<Meeting> {

    /**
     *
     * @return set with meeting's id in which speaker are involved in by his ID
     */
    Set<Long> getAllMeetingsIdSpeakerInvolvesIn(Long speakerId, Connection c) throws SQLException;

    /**
     *
     * @param meetingId
     * @param c
     * @return Speaker IDs and Topic IDs in which speakers has agreed to take part
     */
    Map<Long, Long> getTopicSpeakerIdMapByMeetingId(Long meetingId, Connection c) throws SQLException;

    /**
     *
     * @param meetingId
     * @param c
     * @return map with Topic IDs and Set with Speaker ID IDs which sent application to this topic
     */
    Map<Long, Set<Long>> getSentApplicationsByMeetingId(Long meetingId, Connection c) throws SQLException;

    boolean proposeTopic(Long meetingId, Long userSessionId, Long topicId, Connection c) throws SQLException;

    boolean acceptProposedTopics(Long topicId, Long speakerId, Long meetingId, Connection c) throws SQLException;

    Map<Long, Long> getProposedTopicsBySpeakerIdMap(Long meetingId, Connection c) throws SQLException;

    boolean cancelProposedTopic(Long topicId, Long speakerId, Connection c) throws SQLException;

    void updateInformation(Meeting meeting, Connection c) throws SQLException;

    List<Long> getParticipantsIdByMeetingId(Long meetingId, Connection c) throws SQLException;
}
