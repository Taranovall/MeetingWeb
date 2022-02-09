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

import static com.meeting.service.connection.ConnectionPool.*;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

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
    public Role getAllUserRolesById(long id) throws DataBaseException {
        Role role = null;
        try (Connection c = getInstance().getConnection()) {
            c.setAutoCommit(true);
            role = userDao.getUserRole(id, c);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataBaseException("Cannot get user roles", e);
        }
        return role;
    }

    @Override
    public User getUserByLogin(String login) {
        User user = null;
        try (Connection c = getInstance().getConnection()) {
            c.setAutoCommit(true);
            Optional<User> userOptional = userDao.getUserByLogin(login, c);
            if (userOptional.isPresent()) {
                user = userOptional.get();
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

    @Override
    public User getUserById(Long id) {
        User user = null;
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            c.setAutoCommit(true);
            Optional<User> userOptional = userDao.getById(id, c);
            if (userOptional.isPresent()) {
                user = userOptional.get();
            }
        } catch (SQLException | DataBaseException e) {
            e.printStackTrace();
        }
        return user;
    }
}
