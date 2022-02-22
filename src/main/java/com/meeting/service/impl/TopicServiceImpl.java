package com.meeting.service.impl;

import com.meeting.dao.TopicDao;
import com.meeting.dao.impl.TopicDaoImpl;
import com.meeting.entitiy.Topic;
import com.meeting.exception.DataBaseException;
import com.meeting.service.TopicService;
import com.meeting.service.connection.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TopicServiceImpl implements TopicService {

    private static final Logger log = LogManager.getLogger(TopicServiceImpl.class);

    private TopicDao topicDao;

    public TopicServiceImpl() {
        this.topicDao = new TopicDaoImpl();
    }

    @Override
    public Topic getById(Long id) throws DataBaseException {
        Topic topic = null;
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            c.setAutoCommit(true);
            Optional<Topic> optionalTopic = topicDao.getById(id, c);
            if (optionalTopic.isPresent()) {
                topic = optionalTopic.get();
            } else {
                throw new SQLException();
            }
        } catch (SQLException e) {
            log.error("Cannot get topic by his ID: {}", id, e);
            throw new DataBaseException("Cannot get topic by his ID: " + id, e);
        }
        return topic;
    }

    @Override
    public Set<Topic> getAllFreeTopicsByMeetingId(Long meetingId) throws DataBaseException {
        Set<Topic> topics = new HashSet<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            c.setAutoCommit(true);
            topics.addAll(topicDao.getAllFreeTopicsByMeetingId(meetingId, c));
        } catch (SQLException e) {
            log.error("Cannot get all free topics by meeting ID: {}", meetingId, e);
            throw new DataBaseException("Cannot get all free topics by meeting ID: " + meetingId, e);
        }
        return topics;
    }

    public void setTopicDao(TopicDao topicDao) {
        this.topicDao = topicDao;
    }
}
