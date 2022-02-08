package com.meeting.service;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface MeetingService {

   void createMeeting(User sessionUser, Meeting meeting, String[] topics, String[] speakers, File image);

   Meeting getMeetingById(Long id) throws DataBaseException;

   List<Meeting> getAllMeetings();

   /**
    *
    * @return set with meetings in which speaker are involved in
    */
   Set<Meeting> getMeetingsSpeakerIsInvolvedIn(Long speakerId);
}
