package com.meeting.dao;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Speaker;
import com.meeting.entitiy.Topic;

import java.sql.Connection;
import java.sql.SQLException;

public interface SpeakerDao extends Dao<Speaker>{

    /**
     * Sends offer to this current user to be at the meeting as a speaker.
     * User must have role "Speaker".
     *
     * @param speaker this current user
     * @param meeting in which user is being offered to be a speaker
     * @param topic that has been offered to the user
     */
     boolean sendInvite(Speaker speaker, Meeting meeting, Topic topic, Connection c) throws SQLException;


}
