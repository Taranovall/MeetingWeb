package com.meeting.service;

import com.meeting.entitiy.Meeting;

import java.io.File;
import java.util.List;

public interface MeetingService {

   void createMeeting(Meeting meeting, String[] topics, String[] speakers, File image);

   List<Meeting> getAllMeetings();
}
