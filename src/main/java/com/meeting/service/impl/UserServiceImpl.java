package com.meeting.service.impl;

import com.meeting.connection.ConnectionPool;
import com.meeting.dao.UserDao;
import com.meeting.dao.impl.UserDaoImpl;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static com.meeting.connection.ConnectionPool.close;
import static com.meeting.connection.ConnectionPool.getInstance;
import static com.meeting.connection.ConnectionPool.rollback;
import static com.meeting.util.Constant.CANNOT_GET_USER_BY_ID;
import static com.meeting.util.Constant.CANNOT_GET_USER_BY_LOGIN;
import static com.meeting.util.Constant.CANNOT_SIGN_UP_USER;
import static com.meeting.util.Constant.USER_CANNOT_PARTICIPATE_ID;
import static com.meeting.util.Constant.USER_CANNOT_STOP_PARTICIPATING_ID;

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
            log.error(CANNOT_SIGN_UP_USER, e);
            rollback(c);
            throw new DataBaseException(CANNOT_SIGN_UP_USER, e);
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
            log.error(CANNOT_GET_USER_BY_LOGIN + login, e);
            throw new UserNotFoundException(CANNOT_GET_USER_BY_LOGIN + login, e);
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
            log.error(CANNOT_GET_USER_BY_ID + id, e);
            throw new UserNotFoundException(CANNOT_GET_USER_BY_ID + id, e);
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
            log.error(USER_CANNOT_PARTICIPATE_ID + userId, e);
            throw new DataBaseException(USER_CANNOT_PARTICIPATE_ID + userId, e);
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
            log.error(USER_CANNOT_STOP_PARTICIPATING_ID + userId, e);
            throw new DataBaseException(USER_CANNOT_STOP_PARTICIPATING_ID + userId, e);
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
