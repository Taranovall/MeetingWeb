package com.meeting.service;

import com.meeting.entitiy.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface ValidationService {

    User registrationValidator(HttpServletRequest req);

    User authValidator(HttpServletRequest req);

    boolean isQueryValid (String query, HttpSession session);
}
