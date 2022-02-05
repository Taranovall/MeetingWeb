package com.meeting.service.impl;

import com.meeting.dao.SpeakerDao;
import com.meeting.dao.impl.SpeakerDaoImpl;
import com.meeting.entitiy.Speaker;
import com.meeting.exception.DataBaseException;
import com.meeting.service.SpeakerService;
import com.meeting.service.connection.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.meeting.service.connection.ConnectionPool.*;

public class SpeakerServiceImpl implements SpeakerService {

    private final SpeakerDao speakerDao;

    public SpeakerServiceImpl() {
        this.speakerDao = new SpeakerDaoImpl();
    }

    @Override
    public List<Speaker> getAllSpeakers() {
        List<Speaker> speakers = null;
        try (Connection c = getInstance().getConnection()) {
            c.setAutoCommit(true);
            speakers = speakerDao.getAll(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return speakers;
    }

    @Override
    public Speaker getSpeakerByTopicId(Long topicId) {
        Speaker speaker = null;
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            c.setAutoCommit(true);
            Optional<Speaker> optionalSpeaker = speakerDao.getSpeakerByTopicId(topicId, c);
            if (optionalSpeaker.isPresent()) {
                speaker = optionalSpeaker.get();
            }
        } catch (SQLException | DataBaseException e) {
            e.printStackTrace();
        }
        return speaker;
    }

    @Override
    public Speaker getSpeakerById(Long id) {
        Speaker speaker = null;
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            c.setAutoCommit(true);
           speaker = speakerDao.getById(id, c).get();
        } catch (SQLException | DataBaseException e) {
            e.printStackTrace();
        }
        return speaker;
    }

    @Override
    public List<String> getSentApplication(Long speakerId) {
        List<String> applications = new ArrayList<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            applications.addAll(speakerDao.getSentApplicationBySpeakerId(speakerId, c));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    @Override
    public boolean sendApplication(Long topicId, Long userSessionId) {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            speakerDao.sendInvite(userSessionId, topicId, userSessionId, c);
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            rollback(c);
        } finally {
            close(c);
        }
        return true;
    }

    @Override
    public boolean removeApplication(Long topicId, Long userSessionId) {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            speakerDao.rollbackInvite(userSessionId, topicId, c);
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            rollback(c);
        } finally {
            close(c);
        }
        return true;
    }
}
