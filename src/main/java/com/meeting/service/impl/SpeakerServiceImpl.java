package com.meeting.service.impl;

import com.meeting.connection.ConnectionPool;
import com.meeting.dao.SpeakerDao;
import com.meeting.dao.impl.SpeakerDaoImpl;
import com.meeting.entitiy.Speaker;
import com.meeting.exception.DataBaseException;
import com.meeting.service.SpeakerService;
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
import static com.meeting.util.Constant.CANNOT_ACCEPT_APPLICATION;
import static com.meeting.util.Constant.CANNOT_ACCEPT_INVITATION;
import static com.meeting.util.Constant.CANNOT_CANCEL_INVITATION;
import static com.meeting.util.Constant.CANNOT_GET_ALL_SPEAKERS;
import static com.meeting.util.Constant.CANNOT_GET_RECEIVED_BY_SPEAKER_APPLICATIONS_BY_HIS_ID;
import static com.meeting.util.Constant.CANNOT_GET_SENT_APPLICATIONS_BY_SPEAKER_BY_HIS_ID;
import static com.meeting.util.Constant.CANNOT_GET_SPEAKER_BY_ID;
import static com.meeting.util.Constant.CANNOT_REMOVE_APPLICATION;
import static com.meeting.util.Constant.CANNOT_SEND_APPLICATION;
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
            log.error(CANNOT_GET_ALL_SPEAKERS);
            throw new DataBaseException(CANNOT_GET_ALL_SPEAKERS, e);
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
            log.error(CANNOT_GET_SPEAKER_BY_ID + id, e);
            throw new DataBaseException(CANNOT_GET_SPEAKER_BY_ID + id, e);
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
            log.error(CANNOT_ACCEPT_APPLICATION, e);
            rollback(c);
            throw new DataBaseException(CANNOT_ACCEPT_APPLICATION, e);
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
            log.error(CANNOT_GET_SENT_APPLICATIONS_BY_SPEAKER_BY_HIS_ID + speakerId, e);
            throw new DataBaseException(CANNOT_GET_SENT_APPLICATIONS_BY_SPEAKER_BY_HIS_ID + speakerId, e);
        }
        return applications;
    }

    @Override
    public List<String> getReceivedApplications(Long speakerId) throws DataBaseException {
        List<String> applications = new ArrayList<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            applications.addAll(speakerDao.getApplicationBySpeakerId(speakerId, GET_RECEIVED_APPLICATIONS_BY_SPEAKER_ID_SQL, c));
        } catch (SQLException e) {
            log.error(CANNOT_GET_RECEIVED_BY_SPEAKER_APPLICATIONS_BY_HIS_ID + speakerId, e);
            throw new DataBaseException(CANNOT_GET_RECEIVED_BY_SPEAKER_APPLICATIONS_BY_HIS_ID + speakerId, e);

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
            log.error(CANNOT_ACCEPT_INVITATION, e);
            rollback(c);
            throw new DataBaseException(CANNOT_ACCEPT_INVITATION, e);
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
            log.error(CANNOT_CANCEL_INVITATION, e);
            rollback(c);
            throw new DataBaseException(CANNOT_CANCEL_INVITATION, e);
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
            log.error(CANNOT_SEND_APPLICATION, e);
            rollback(c);
            throw new DataBaseException(CANNOT_SEND_APPLICATION, e);
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
            log.error(CANNOT_REMOVE_APPLICATION, e);
            rollback(c);
            throw new DataBaseException(CANNOT_REMOVE_APPLICATION, e);
        } finally {
            close(c);
        }
        return true;
    }

    public void setSpeakerDao(SpeakerDao speakerDao) {
        this.speakerDao = speakerDao;
    }
}
