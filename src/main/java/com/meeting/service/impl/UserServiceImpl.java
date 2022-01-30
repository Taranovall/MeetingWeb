package com.meeting.service.impl;

import com.meeting.dao.UserDao;
import com.meeting.dao.impl.UserDaoImpl;
import com.meeting.entitiy.Role;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.UserService;
import com.meeting.service.connection.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import static com.meeting.service.connection.ConnectionPool.*;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    //TODO: check Dependency Injection pattern
    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    //TODO: add logs
    @Override
    public void signUpUser(User user) throws DataBaseException {
        Connection c = null;
        try {
            c = getInstance().getConnection();
            userDao.save(user, c);
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            rollback(c);
            throw new DataBaseException("Cannot sign up user", e);
        } finally {
            close(c);
        }
    }

    @Override
    public Set<Role> getAllUserRolesById(long id) throws DataBaseException {
        Set<Role> roles;
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            c.setAutoCommit(true);
            roles = userDao.getAllUserRoles(id, c);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException("Cannot get user roles", e);
        }
        return roles;
    }

    @Override
    public User getUserByLogin(String login) {
        User user = null;
        try (Connection c = getInstance().getConnection()) {
            c.setAutoCommit(true);
            Optional<User> userOpt = userDao.getUserByLogin(login, c);
            if (userOpt.isPresent()) {
                user = userOpt.get();
            } else {
                Locale locale = new Locale("en");
                String message = ResourceBundle.getBundle("message", locale).getString("user.notfound");
                throw new UserNotFoundException(MessageFormat.format(message, login));
            }
        } catch (SQLException | UserNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }
}
