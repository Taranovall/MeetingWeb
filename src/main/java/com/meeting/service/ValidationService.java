package com.meeting.service;

import com.meeting.entitiy.User;

import javax.servlet.http.HttpServletRequest;

public interface ValidationService {

    User registrationValidator(HttpServletRequest req);

    User authValidator(HttpServletRequest req);
}
