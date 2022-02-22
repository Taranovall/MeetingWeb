package com.meeting.service;

import com.meeting.entitiy.Speaker;
import com.meeting.exception.DataBaseException;

import java.util.List;

public interface SpeakerService {

    List<Speaker> getAllSpeakers() throws DataBaseException;

    Speaker getSpeakerById(Long id) throws DataBaseException;
    /**
     * Speaker sends application to be a speaker of a particular topic
     * @return true if the function was successful
     */
    boolean sendApplication(Long topicId, Long userSessionId) throws DataBaseException;

    /**
     * Speaker cancels pre-sent application
     * @return true if the function was successful
     */
    boolean removeApplication(Long topicId, Long speakerId) throws DataBaseException;

    /**
     * Function for accepting speaker's application
     * @return true if the function was successful
     */
    boolean acceptApplication(Long topicId, Long speakerId) throws DataBaseException;

    /**
     * Function to accept the invitation to be a speaker
     * @return true if the function was successful
     */
    boolean acceptInvitation(Long topicId, Long speakerId) throws DataBaseException;

    /**
     * Function to cancel the invitation to be a speaker
     * @return true if the function was successful
     */
    boolean cancelInvitation(Long topicId, Long speakerId) throws DataBaseException;

    /**
     * @return list with applications sent by speaker
     */
    List<String> getSentApplications(Long speakerId) throws DataBaseException;

    /**
     *
     * @param speakerId
     * @return list with applications that user received
     */
    List<String> getReceivedApplications(Long speakerId) throws DataBaseException;

}
