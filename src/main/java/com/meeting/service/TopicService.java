package com.meeting.service;

import com.meeting.entitiy.Topic;

import java.util.Set;

public interface TopicService {

    Topic getById(Long id);

    Set<Topic> getAllFreeTopicsByMeetingId(Long meetingId);
}
