package com.meeting.service.impl;

import com.meeting.dao.SpeakerDao;
import com.meeting.dao.impl.SpeakerDaoImpl;
import com.meeting.entitiy.Speaker;
import com.meeting.service.SpeakerService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.meeting.service.connection.ConnectionPool.getInstance;

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
}
