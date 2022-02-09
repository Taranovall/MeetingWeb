package com.meeting.service.impl;

import com.meeting.dao.TopicDao;
import com.meeting.dao.impl.TopicDaoImpl;
import com.meeting.entitiy.Topic;
import com.meeting.exception.DataBaseException;
import com.meeting.service.TopicService;
import com.meeting.service.connection.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class TopicServiceImpl implements TopicService {

    private final TopicDao topicDao;

    public TopicServiceImpl() {
        this.topicDao = new TopicDaoImpl();
    }

    @Override
    public Topic getById(Long id) {
        Topic topic = null;
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            c.setAutoCommit(true);
            topic = topicDao.getById(id, c).get();
        } catch (SQLException | DataBaseException e) {
            e.printStackTrace();
        }
        return topic;
    }

    @Override
    public Set<Topic> getAllFreeTopicsByMeetingId(Long meetingId) {
        Set<Topic> topics = new HashSet<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            c.setAutoCommit(true);
            topics.addAll(topicDao.getAllFreeTopicsByMeetingId(meetingId, c));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topics;
    }
}
