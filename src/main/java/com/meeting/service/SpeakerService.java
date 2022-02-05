package com.meeting.service;

import com.meeting.entitiy.Speaker;

import java.util.List;

public interface SpeakerService {

    List<Speaker> getAllSpeakers();

    Speaker getSpeakerById(Long id);

    Speaker getSpeakerByTopicId(Long topicId);

    boolean sendApplication(Long topicId, Long userSessionId);

    boolean removeApplication(Long topicId, Long userSessionId);

    List<String> getSentApplication(Long speakerId);
}
