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

    Map<Topic, Set<Speaker>> getSentApplicationMapByMeetingId(Long meetingId);

    boolean proposeTopic(Long meetingId, Long userSessionId, String topicName);

    boolean acceptProposedTopic(Long topicId, Long speakerId, Long meetingId);

    boolean cancelProposedTopic(Long topicId, Long speakerId);

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

}
