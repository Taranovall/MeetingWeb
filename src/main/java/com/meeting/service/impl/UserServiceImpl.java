package com.meeting.service.impl;

import com.meeting.dao.UserDao;
import com.meeting.dao.impl.UserDaoImpl;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.UserService;
import com.meeting.service.connection.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static com.meeting.service.connection.ConnectionPool.close;
import static com.meeting.service.connection.ConnectionPool.getInstance;
import static com.meeting.service.connection.ConnectionPool.rollback;

public class UserServiceImpl implements UserService {

    private static final Logger log = LogManager.getLogger(UserServiceImpl.class);

    private UserDao userDao;

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
            log.error("Cannot sign up user", e);
            rollback(c);
            throw new DataBaseException("Cannot sign up user", e);
        } finally {
            close(c);
        }
    }

    @Override
    public User getUserByLogin(String login) throws UserNotFoundException {
        User user = null;
        try (Connection c = getInstance().getConnection()) {
            c.setAutoCommit(true);
            Optional<User> userOptional = userDao.getUserByLogin(login, c);
            if (userOptional.isPresent()) {
                user = userOptional.get();
            } else {
                throw new SQLException();
            }
        } catch (SQLException e) {
            log.error("Cannot get user by login: {}", login, e);
            throw new UserNotFoundException("Cannot get user by login: " + login, e);
        }
        return user;
    }

    @Override
    public User getUserById(Long id) throws UserNotFoundException {
        User user = null;
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            c.setAutoCommit(true);
            Optional<User> userOptional = userDao.getById(id, c);
            if (userOptional.isPresent()) {
                user = userOptional.get();
            } else {
                throw new SQLException();
            }
        } catch (SQLException e) {
            log.error("Cannot get user by ID: {}", id, e);
            throw new UserNotFoundException("Cannot get user by ID: " + id, e);
        }
        return user;
    }

    @Override
    public void participate(Long userId, Long meetingId) throws DataBaseException {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            userDao.participate(userId, meetingId, c);
            c.commit();
        } catch (SQLException e) {
            rollback(c);
            log.error("User {} cannot participate", userId, e);
            throw new DataBaseException("User cannot participate", e);
        } finally {
            close(c);
        }
    }

    @Override
    public void stopParticipating(Long userId, Long meetingId) throws DataBaseException {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            userDao.stopParticipating(userId, meetingId, c);
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            rollback(c);
            log.error("User {} cannot stop participating", userId, e);
            throw new DataBaseException("User cannot stop participating", e);
        } finally {
            close(c);
        }
    }

    @Override
    public boolean setEmail(Long userId, String email) throws DataBaseException {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            userDao.setEmail(userId, email, c);
            c.commit();
            log.info("Email '{}' for user with ID {} was set successful", email, userId);
        } catch (SQLException e) {
            log.error("Cannot set email '{}' for user with ID {}", email, userId, e);
            String message = String.format("Cannot set email '%s' for user with ID %s", email, userId);
            rollback(c);
            throw new DataBaseException(message, e);
        } finally {
            close(c);
        }
        return true;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
