package com.meeting.service;

import com.meeting.entitiy.Speaker;

import java.util.List;
import java.util.Set;

public interface SpeakerService {

    List<Speaker> getAllSpeakers();

    Speaker getSpeakerById(Long id);

    Speaker getSpeakerByTopicId(Long topicId);

    boolean sendApplication(Long topicId, Long userSessionId);

    boolean removeApplication(Long topicId, Long speakerId);

    boolean acceptApplication(Long topicId, Long speakerId);

    boolean acceptInvitation(Long topicId, Long speakerId);

    boolean cancelInvitation(Long topicId, Long speakerId);

    List<String> getSentApplications(Long speakerId);
    
    List<String> getReceivedApplications(Long speakerId);

    Set<Speaker> getAllSpeakerApplicationsByTopicId(Long topicId);
}
