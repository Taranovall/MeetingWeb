package com.meeting.service;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

public interface ValidationService {

    User registrationValidator(HttpServletRequest req);

    User authValidator(HttpServletRequest req);

    /**
     * @return true only if query is valid
     */
    boolean searchValidator(String query, HttpSession session);

    /**
     * @return true if fields are valid otherwise return false and set attribute 'error' with message
     */
    boolean createMeetingGetValidator(Meeting meeting, HttpServletRequest request);

    /**
     * @return true if fields are valid
     */
    boolean createMeetingPostValidator(String[] topics, Part uploadedImage, HttpServletRequest req);
}
