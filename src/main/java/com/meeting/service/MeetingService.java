package com.meeting.service;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Speaker;
import com.meeting.entitiy.Topic;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.EmailException;
import com.meeting.exception.UserNotFoundException;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MeetingService {

    void createMeeting(User sessionUser, Meeting meeting, String[] topics, String[] speakers, File image) throws DataBaseException;

    Meeting getMeetingById(Long id) throws DataBaseException, UserNotFoundException;

    List<Meeting> getAllMeetings() throws DataBaseException, UserNotFoundException;

    /**
     * @return set with meetings in which speaker is involved in
     */
    Set<Meeting> getMeetingsSpeakerIsInvolvedIn(Long speakerId) throws DataBaseException, UserNotFoundException;

    /**
     * @param meetingId
     * @return map with topic set in which speaker agreed to take part
     */
    Map<Speaker, Set<Topic>> getAcceptedTopicsMapByMeetingId(Long meetingId) throws DataBaseException;

    /**
     *
     * @param meetingId
     * @return map with topics(key) and set of speakers(value) which sent application to be a speaker in topic which is a key
     */
    Map<Topic, Set<Speaker>> getSentApplicationMapByMeetingId(Long meetingId) throws DataBaseException;

    /**
     * Function for speaker to propose his own topic for particular meeting
     * @param meetingId
     * @param userSessionId speaker's ID
     * @param topicName
     * @return true if the function was successful
     */
    boolean proposeTopic(Long meetingId, Long userSessionId, String topicName) throws DataBaseException;

    /**
     * Function for moderator to accept proposed topic by speaker
     * @return true if the function was successful
     */
    boolean acceptProposedTopic(Long topicId, Long speakerId, Long meetingId) throws DataBaseException;

    /**
     * Function for moderator to cancel proposed topic by speaker
     * @return true if the function was successful
     */
    boolean cancelProposedTopic(Long topicId) throws DataBaseException;

    /**
     *
     * @param meetingId
     * @return map in which key is a topic, which is proposed by speaker(value)
     */
    Map<Topic, Speaker> getProposedTopicsBySpeakerByMeetingId(Long meetingId) throws DataBaseException;

    /**
     * Updates meeting's name, start time, end time, place, date
     */
    void updateInformation(Meeting meeting, Meeting meetingBeforeUpdating) throws DataBaseException, EmailException, UserNotFoundException;


    List<User> getParticipantsByMeetingId(Long meetingId) throws DataBaseException, UserNotFoundException;

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

    Set<Speaker> getSpeakersByMeetingId(Long meetingId) throws DataBaseException;
}
