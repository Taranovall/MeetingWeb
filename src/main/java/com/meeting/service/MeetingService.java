package com.meeting.service;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;

import java.io.File;
import java.util.List;

public interface MeetingService {

   void createMeeting(User sessionUser, Meeting meeting, String[] topics, String[] speakers, File image);

   Meeting getMeetingById(Long id) throws DataBaseException;

   List<Meeting> getAllMeetings();
}
