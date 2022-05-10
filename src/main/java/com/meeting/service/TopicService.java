package com.meeting.service;

import com.meeting.entitiy.Topic;
import com.meeting.exception.DataBaseException;

import java.util.Set;

public interface TopicService {

    boolean isTopicExist(String topicName, Long meetingId) throws DataBaseException;

    Topic getById(Long id) throws DataBaseException;

    Set<Topic> getAllFreeTopicsByMeetingId(Long meetingId) throws DataBaseException;
}
