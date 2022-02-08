package com.meeting.dao;

import com.meeting.entitiy.Meeting;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public interface MeetingDao extends Dao<Meeting> {

    /**
     *
     * @return set with meeting's id in which speaker are involved in by his ID
     */
    Set<Long> getAllMeetingsIdSpeakerInvolvesIn(Long speakerId, Connection c) throws SQLException;
}
