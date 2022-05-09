package com.meeting.service.impl;

import com.meeting.dao.SpeakerDao;
import com.meeting.dao.impl.SpeakerDaoImpl;
import com.meeting.entitiy.Speaker;
import com.meeting.exception.DataBaseException;
import com.meeting.service.SpeakerService;
import com.meeting.connection.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.meeting.connection.ConnectionPool.close;
import static com.meeting.connection.ConnectionPool.getInstance;
import static com.meeting.connection.ConnectionPool.rollback;
import static com.meeting.util.SQLQuery.GET_RECEIVED_APPLICATIONS_BY_SPEAKER_ID_SQL;
import static com.meeting.util.SQLQuery.GET_SENT_APPLICATIONS_BY_SPEAKER_ID_SQL;

public class SpeakerServiceImpl implements SpeakerService {

    private static final Logger log = LogManager.getLogger(SpeakerServiceImpl.class);

    private SpeakerDao speakerDao;

    public SpeakerServiceImpl() {
        this.speakerDao = new SpeakerDaoImpl();
    }

    @Override
    public List<Speaker> getAllSpeakers() throws DataBaseException {
        List<Speaker> speakers = null;
        try (Connection c = getInstance().getConnection()) {
            c.setAutoCommit(true);
            speakers = speakerDao.getAll(c);
        } catch (SQLException e) {
            log.error("Cannot get all speakers");
            throw new DataBaseException("Cannot get all speakers", e);
        }
        return speakers;
    }

    @Override
    public Speaker getSpeakerById(Long id) throws DataBaseException {
        Speaker speaker = null;
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            c.setAutoCommit(true);
            Optional<Speaker> speakerOptional = speakerDao.getById(id, c);
            if (speakerOptional.isPresent()) {
                speaker = speakerOptional.get();
            } else {
                throw new SQLException();
            }
        } catch (SQLException e) {
            log.error("Cannot get speaker by ID: {}", id, e);
            throw new DataBaseException("Cannot get speaker by ID: " + id, e);
        }
        return speaker;
    }

    @Override
    public boolean acceptApplication(Long topicId, Long speakerId) throws DataBaseException {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            speakerDao.acceptApplication(speakerId, topicId, c);
            c.commit();
        } catch (SQLException e) {
            log.error("Cannot accept application", e);
            rollback(c);
            throw new DataBaseException("Cannot accept application", e);
        } finally {
            close(c);
        }
        return true;
    }

    @Override
    public List<String> getSentApplications(Long speakerId) throws DataBaseException {
        List<String> applications = new ArrayList<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            applications.addAll(speakerDao.getApplicationBySpeakerId(speakerId, GET_SENT_APPLICATIONS_BY_SPEAKER_ID_SQL, c));
        } catch (SQLException e) {
            log.error("Cannot get sent applications by speaker by his ID: {}", speakerId, e);
            throw new DataBaseException("Cannot get sent applications by speaker by his ID: " + speakerId, e);
        }
        return applications;
    }

    @Override
    public List<String> getReceivedApplications(Long speakerId) throws DataBaseException {
        List<String> applications = new ArrayList<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            applications.addAll(speakerDao.getApplicationBySpeakerId(speakerId, GET_RECEIVED_APPLICATIONS_BY_SPEAKER_ID_SQL, c));
        } catch (SQLException e) {
            log.error("Cannot get received by speaker applications by his ID: {}", speakerId, e);
            throw new DataBaseException("Cannot get received by speaker applications by his ID: " + speakerId, e);

        }
        return applications;
    }

    @Override
    public boolean acceptInvitation(Long topicId, Long speakerId) throws DataBaseException {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            speakerDao.acceptApplication(speakerId, topicId, c);
            c.commit();
        } catch (SQLException e) {
            log.error("Cannot accept invitation", e);
            rollback(c);
            throw new DataBaseException("Cannot accept invitation", e);
        } finally {
            close(c);
        }
        return true;
    }

    @Override
    public boolean cancelInvitation(Long topicId, Long userSessionId) throws DataBaseException {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            speakerDao.rollbackInvite(userSessionId, topicId, c);
            c.commit();
        } catch (SQLException e) {
            log.error("Cannot cancel invitation", e);
            rollback(c);
            throw new DataBaseException("Cannot cancel invitation", e);
        } finally {
            close(c);
        }
        return true;
    }

    @Override
    public boolean sendApplication(Long topicId, Long userSessionId) throws DataBaseException {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            speakerDao.sendInvite(userSessionId, topicId, userSessionId, c);
            c.commit();
        } catch (SQLException e) {
            log.error("Cannot send application", e);
            rollback(c);
            throw new DataBaseException("Cannot send application", e);
        } finally {
            close(c);
        }
        return true;
    }

    @Override
    public boolean removeApplication(Long topicId, Long speakerId) throws DataBaseException {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            speakerDao.rollbackInvite(speakerId, topicId, c);
            c.commit();
        } catch (SQLException e) {
            log.error("Cannot remove application", e);
            rollback(c);
            throw new DataBaseException("Cannot remove application", e);
        } finally {
            close(c);
        }
        return true;
    }

    public void setSpeakerDao(SpeakerDao speakerDao) {
        this.speakerDao = speakerDao;
    }
}
