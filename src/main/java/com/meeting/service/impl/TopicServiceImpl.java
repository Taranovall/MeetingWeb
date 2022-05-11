package com.meeting.service.impl;

import com.meeting.connection.ConnectionPool;
import com.meeting.dao.TopicDao;
import com.meeting.dao.impl.TopicDaoImpl;
import com.meeting.entitiy.Topic;
import com.meeting.exception.DataBaseException;
import com.meeting.service.TopicService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.meeting.util.Constant.CANNOT_GET_ALL_FREE_TOPICS_BY_MEETING_ID;
import static com.meeting.util.Constant.CANNOT_GET_TOPIC_BY_HIS_ID;
import static com.meeting.util.Constant.CANNOT_GET_TOPIC_BY_NAME;

public class TopicServiceImpl implements TopicService {

    private static final Logger log = LogManager.getLogger(TopicServiceImpl.class);

    private TopicDao topicDao;

    public TopicServiceImpl() {
        this.topicDao = new TopicDaoImpl();
    }

    @Override
    public boolean isTopicExist(String topicName, Long meetingId) throws DataBaseException {
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            return topicDao.isTopicExist(topicName, meetingId, c);
        } catch (SQLException e) {
            log.error("Cannot get topic by name: '{}'", topicName, e);
            throw new DataBaseException(CANNOT_GET_TOPIC_BY_NAME + topicName, e);
        }
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
            log.error("Cannot get topic by his ID: " + id, e);
            throw new DataBaseException(CANNOT_GET_TOPIC_BY_HIS_ID + id, e);
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
            log.error(CANNOT_GET_ALL_FREE_TOPICS_BY_MEETING_ID + meetingId, e);
            throw new DataBaseException(CANNOT_GET_ALL_FREE_TOPICS_BY_MEETING_ID + meetingId, e);
        }
        return topics;
    }

    public void setTopicDao(TopicDao topicDao) {
        this.topicDao = topicDao;
    }
}
