package com.meeting.service;

import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.UserNotFoundException;

public interface UserService {

    /**
     * Signs up user in the site
     * @param user
     * @throws DataBaseException
     */
    void signUpUser(User user) throws DataBaseException;

    User getUserByLogin(String login) throws UserNotFoundException;

    User getUserById(Long id) throws UserNotFoundException;

    /**
     * puts userId and meetingId into table meeting_participants
     */
    void participate(Long userId, Long meetingId) throws DataBaseException;

    /**
     * removes userId and meetingId from table meeting_participants
     */
    void stopParticipating(Long userId, Long meetingId) throws DataBaseException;
}
