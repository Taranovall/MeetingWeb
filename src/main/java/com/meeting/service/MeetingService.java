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
    *
    * @return set with meetings in which speaker are involved in
    */
   Set<Meeting> getMeetingsSpeakerIsInvolvedIn(Long speakerId);

   /**
    *
    * @param meetingId
    * @return map with topic set in which speaker agreed to take part
    */
   Map<Speaker, Set<Topic>> getAcceptedTopicsMapByMeetingId(Long meetingId);

   Map<Topic, Set<Speaker>> getSentApplicationMapByMeetingId(Long meetingId);

   boolean proposeTopic(Long meetingId, Long userSessionId, String topicName);

   boolean acceptProposedTopic(Long topicId, Long speakerId, Long meetingId);

   boolean cancelProposedTopic(Long topicId, Long speakerId);

   Map<Topic, Speaker> getProposedTopicsBySpeakerByMeetingId(Long meetingId);

    void updateInformation(Meeting meeting) throws DataBaseException;
}
