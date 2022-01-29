package com.meeting.dao.impl;

import com.meeting.dao.SpeakerDao;
import com.meeting.dao.TopicDao;
import com.meeting.dao.UserDao;
import com.meeting.entitiy.*;
import com.meeting.util.SQLQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SpeakerDaoImpl implements SpeakerDao {

    private final TopicDao topicDao;
    private final UserDao userDao;

    public SpeakerDaoImpl() {
        this.topicDao = new TopicDaoImpl();
        this.userDao = new UserDaoImpl();
    }

    @Override
    public Optional<Speaker> getById(long id, Connection c) throws SQLException {
        return Optional.empty();
    }

    @Override
    public List<Speaker> getAll(Connection c) throws SQLException {
        List<Speaker> speakers = new LinkedList<>();
        List<User> usersWithRoleSpeaker = userDao.getAllUserByRole(Role.SPEAKER.name(), c);

        for (User user : usersWithRoleSpeaker) {
            speakers.add(new Speaker(user.getId(), user.getLogin()));
        }
        return speakers;
    }

    @Override
    public void save(Speaker speaker, Connection c) throws SQLException {

    }

    @Override
    public void delete(Speaker speaker, Connection c) {

    }

    @Override
    public boolean sendInvite(Speaker speaker, Meeting meeting, Topic topic, Connection c) throws SQLException {
        if (speaker.getLogin().equals("none")) {
            topicDao.freeTopic(meeting, topic, c);
            return false;
        }

        PreparedStatement p = c.prepareStatement(SQLQuery.INVITE_SPEAKER_TO_MEETING_SQL);

        p.setLong(1, speaker.getId());
        p.setLong(2, meeting.getId());
        p.executeUpdate();

        return true;
    }


}
