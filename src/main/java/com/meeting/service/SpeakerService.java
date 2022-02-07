package com.meeting.service;

import com.meeting.entitiy.Speaker;

import java.util.List;
import java.util.Set;

public interface SpeakerService {

    List<Speaker> getAllSpeakers();

    Speaker getSpeakerById(Long id);

    Speaker getSpeakerByTopicId(Long topicId);

    boolean sendApplication(Long topicId, Long userSessionId);

    boolean removeApplication(Long topicId, Long userId);

    boolean acceptInvitation(Long topicId, Long userId);

    boolean cancelInvitation(Long topicId, Long userId);

    List<String> getSentApplications(Long speakerId);
    
    List<String> getReceivedApplications(Long speakerId);

    Set<Speaker> getAllSpeakerApplicationsByTopicId(Long topicId);
}
