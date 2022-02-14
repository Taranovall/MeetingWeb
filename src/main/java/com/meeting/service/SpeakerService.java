package com.meeting.service;

import com.meeting.entitiy.Speaker;

import java.util.List;

public interface SpeakerService {

    List<Speaker> getAllSpeakers();

    Speaker getSpeakerById(Long id);
    /**
     * Speaker sends application to be a speaker of a particular topic
     * @return true if the function was successful
     */
    boolean sendApplication(Long topicId, Long userSessionId);

    /**
     * Speaker cancels pre-sent application
     * @return true if the function was successful
     */
    boolean removeApplication(Long topicId, Long speakerId);

    /**
     * Function for accepting speaker's application
     * @return true if the function was successful
     */
    boolean acceptApplication(Long topicId, Long speakerId);

    /**
     * Function to accept the invitation to be a speaker
     * @return true if the function was successful
     */
    boolean acceptInvitation(Long topicId, Long speakerId);

    /**
     * Function to cancel the invitation to be a speaker
     * @return true if the function was successful
     */
    boolean cancelInvitation(Long topicId, Long speakerId);

    /**
     * @return list with applications sent by speaker
     */
    List<String> getSentApplications(Long speakerId);

    /**
     *
     * @param speakerId
     * @return list with applications that user received
     */
    List<String> getReceivedApplications(Long speakerId);
}
