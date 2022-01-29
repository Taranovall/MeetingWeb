package com.meeting.dao;

import com.meeting.entitiy.Role;
import com.meeting.entitiy.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserDao extends Dao<User> {

    Set<Role> getAllUserRoles(long id, Connection c) throws SQLException;

    void addRoleForUser(Long id, Connection c) throws SQLException;

    Optional<User> getUserByLogin(String login, Connection c) throws SQLException;

    List<User> getAllUserByRole(String role, Connection c) throws SQLException;



}
