package com.meeting.service;

import com.meeting.entitiy.Role;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;

public interface UserService {

    void signUpUser(User user) throws DataBaseException;

    Role getAllUserRolesById(long id) throws DataBaseException;

    User getUserByLogin(String login);

    User getUserById(Long id);
}
