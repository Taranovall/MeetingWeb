package com.meeting.service;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Speaker;
import com.meeting.entitiy.Topic;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MeetingService {

    void createMeeting(User sessionUser, Meeting meeting, String[] topics, String[] speakers, File image) throws DataBaseException;

    Meeting getMeetingById(Long id) throws DataBaseException;

    List<Meeting> getAllMeetings() throws DataBaseException;

    /**
     * @return set with meetings in which speaker are involved in
     */
    Set<Meeting> getMeetingsSpeakerIsInvolvedIn(Long speakerId);

    /**
     * @param meetingId
     * @return map with topic set in which speaker agreed to take part
     */
    Map<Speaker, Set<Topic>> getAcceptedTopicsMapByMeetingId(Long meetingId);

    /**
     *
     * @param meetingId
     * @return map with topics(key) and set of speakers(value) which sent application to be a speaker in topic which is a key
     */
    Map<Topic, Set<Speaker>> getSentApplicationMapByMeetingId(Long meetingId);

    /**
     * Function for speaker to propose his own topic for particular meeting
     * @param meetingId
     * @param userSessionId speaker's ID
     * @param topicName
     * @return true if the function was successful
     */
    boolean proposeTopic(Long meetingId, Long userSessionId, String topicName);

    /**
     * Function for moderator to accept proposed topic by speaker
     * @return true if the function was successful
     */
    boolean acceptProposedTopic(Long topicId, Long speakerId, Long meetingId);

    /**
     * Function for moderator to cancel proposed topic by speaker
     * @return true if the function was successful
     */
    boolean cancelProposedTopic(Long topicId, Long speakerId);

    /**
     *
     * @param meetingId
     * @return map in which key is a topic, which is proposed by speaker(value)
     */
    Map<Topic, Speaker> getProposedTopicsBySpeakerByMeetingId(Long meetingId);

    /**
     * Updates meeting's name, start time, end time, place, date
     */
    void updateInformation(Meeting meeting) throws DataBaseException;


    List<User> getParticipantsByMeetingId(Long meetingId);

    /**
     * Sets presence true for users with ID from presentUser
     *
     * @param presentUsers array with user's id which have been selected in checkbox
     * @param meetingId    meeting at which they are present
     */
    void markPresentUsers(String[] presentUsers, Long meetingId) throws DataBaseException;

    /**
     * @param meetingId
     * @return list of present users' id
     */
    List<Long> getPresentUserIds(Long meetingId) throws DataBaseException;

    /**
     * @param meetingId
     * @return the percentage of participants who were physically present
     */
    double getAttendancePercentageByMeetingId(Long meetingId) throws DataBaseException;

    /**
     *
     * @return true if meeting is already started
     */
    boolean isMeetingStarted(Meeting meeting);

    /**
     *
     * @return true if meeting is going on right now
     */
    boolean isMeetingGoingOnNow(Meeting meeting);

    /**
     *
     * @return true if meeting is passed
     */
    boolean isMeetingPassed(Meeting meeting);
}
