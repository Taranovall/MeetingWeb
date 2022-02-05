package com.meeting.service.impl;

import com.meeting.dao.TopicDao;
import com.meeting.dao.impl.TopicDaoImpl;
import com.meeting.entitiy.Topic;
import com.meeting.exception.DataBaseException;
import com.meeting.service.TopicService;
import com.meeting.service.connection.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

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
}
