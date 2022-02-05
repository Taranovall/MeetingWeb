package com.meeting.dao.impl;

import com.meeting.dao.UserDao;
import com.meeting.entitiy.Role;
import com.meeting.entitiy.User;

import java.sql.*;
import java.util.*;

import static com.meeting.util.SQLQuery.*;

public class UserDaoImpl implements UserDao {

    @Override
    public Optional<User> getById(Long id, Connection c) throws SQLException {
        Optional<User> optionalUser = Optional.empty();
        PreparedStatement p = c.prepareStatement(GET_USER_BY_ID_SQL);
        p.setLong(1, id);
        ResultSet rs = p.executeQuery();
        if (rs.next()) {
            User user = extractUser(rs);
            user.setRoles(getAllUserRoles(user.getId(), c));
            optionalUser = Optional.of(user);
        }
        return optionalUser;
    }

    @Override
    public List<User> getAll(Connection c) throws SQLException {
        List<User> users = new LinkedList<>();
        PreparedStatement p = c.prepareStatement(GET_ALL_USERS_SQL);
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            User user = extractUser(rs);
            user.setRoles(getAllUserRoles(user.getId(), c));
            users.add(user);
        }
        return users;
    }

    @Override
    public Set<Role> getAllUserRoles(long id, Connection c) throws SQLException {
        Set<Role> roles = new HashSet<>();
        PreparedStatement p = c.prepareStatement(GET_ALL_USER_ROLES_BY_ID_SQL);
        p.setLong(1, id);
        ResultSet rs = p.executeQuery();
        while(rs.next()) {
            roles.add(Role.getRoleByString(rs.getString("name")));
        }
        return roles;
    }

    @Override
    public void save(User user, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(CREATE_USER_SQL, Statement.RETURN_GENERATED_KEYS);

        String login = user.getLogin();
        String password = user.getPassword();

        p.setString(1, login);
        p.setString(2, password);

        if (p.executeUpdate() > 0) {
            ResultSet rs = p.getGeneratedKeys();
            if (rs.next()) {
                Long usedId = rs.getLong(1);
                user.addRole(Role.USER);
                addRoleForUser(usedId, c);
            }
        }
    }

    @Override
    public Optional<User> getUserByLogin(String login, Connection c) throws SQLException {
        Optional<User> optionalUser = Optional.empty();
        PreparedStatement p = c.prepareStatement(GET_USER_BY_LOGIN_SQL);
        p.setString(1, login);
        ResultSet rs = p.executeQuery();
        if (rs.next()) {
            User user = extractUser(rs);
            user.setRoles(getAllUserRoles(user.getId(), c));
            optionalUser = Optional.of(user);
        }
        return optionalUser;
    }

    @Override
    public List<User> getAllUserByRole(String role, Connection c) throws SQLException {
        List<User> users = new LinkedList<>();
        PreparedStatement p = c.prepareStatement(GET_ALL_USERS_BY_ROLE_SQL);
        p.setString(1, role);
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            users.add(extractUser(rs));
        }
        return users;
    }

    @Override
    public void delete(User user, Connection c) {

    }

    @Override
    public void addRoleForUser(Long id, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(ADD_ROLE_FOR_USER_SQL);
        p.setLong(1, id);
        p.setString(2, Role.USER.name());
        p.executeUpdate();
    }

    private User extractUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String login = rs.getString("login");
        String password = rs.getString("password");
        User user = new User(id, login, password);
        return user;
    }
}
