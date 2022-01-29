package com.meeting.service;

import com.meeting.entitiy.Role;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;

import java.util.Set;

public interface UserService {

    void signUpUser(User user) throws DataBaseException;

    Set<Role> getAllUserRolesById(long id) throws DataBaseException;

    User getUserByLogin(String login);
}
