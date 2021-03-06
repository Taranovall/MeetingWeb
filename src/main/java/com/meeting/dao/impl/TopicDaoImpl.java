package com.meeting.dao.impl;

import com.meeting.dao.TopicDao;
import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Topic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.meeting.util.SQLQuery.CREATE_FREE_TOPIC_SQL;
import static com.meeting.util.SQLQuery.CREATE_TOPIC_SQL;
import static com.meeting.util.SQLQuery.GET_ALL_FREE_TOPICS_BY_MEETING_ID;
import static com.meeting.util.SQLQuery.GET_TOPIC_BY_ID_SQL;
import static com.meeting.util.SQLQuery.GET_TOPIC_BY_NAME_SQL;
import static com.meeting.util.SQLQuery.LINK_TOPIC_WITH_MEETING_SQL;

public class TopicDaoImpl implements TopicDao {

    @Override
    public Optional<Topic> getById(Long id, Connection c) throws SQLException {
        Optional<Topic> optionalTopic = Optional.empty();
        PreparedStatement p = c.prepareStatement(GET_TOPIC_BY_ID_SQL);
        p.setLong(1, id);
        ResultSet rs = p.executeQuery();
        if (rs.next()) {
            optionalTopic = Optional.of(extractTopic(rs));
        }
        return optionalTopic;
    }

    @Override
    public boolean isTopicExist(String topicName, Long meetingId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(GET_TOPIC_BY_NAME_SQL);
        p.setLong(1, meetingId);
        p.setString(2, topicName);
        return p.executeQuery().next();
    }

    @Override
    public Set<Topic> getAllFreeTopicsByMeetingId(Long meetingId, Connection c) throws SQLException {
        Set<Topic> topics = new HashSet<>();
        PreparedStatement p = c.prepareStatement(GET_ALL_FREE_TOPICS_BY_MEETING_ID);
        p.setLong(1, meetingId);
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            Long topicId = rs.getLong("topic_id");
            Optional<Topic> optionalTopic = getById(topicId, c);
            optionalTopic.ifPresent(topics::add);
        }
        return topics;
    }

    @Override
    public void save(Topic topic, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(CREATE_TOPIC_SQL, Statement.RETURN_GENERATED_KEYS);
        String topicName = topic.getName();
        p.setString(1, topicName);

        if (p.executeUpdate() > 0) {
            ResultSet rs = p.getGeneratedKeys();
            if (rs.next()) {
                Long topicId = rs.getLong(1);
                topic.setId(topicId);
            }
        }
    }

    @Override
    public void addTopicsToMeeting(Long meetingId, Set<Topic> topics, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(LINK_TOPIC_WITH_MEETING_SQL);
        p.setLong(1, meetingId);
        for (Topic topic : topics) {
            this.save(topic, c);
            p.setLong(2, topic.getId());
            p.executeUpdate();
        }
    }

    @Override
    public void createFreeTopic(Meeting meeting, Topic topic, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(CREATE_FREE_TOPIC_SQL);
        p.setLong(1, meeting.getId());
        p.setLong(2, topic.getId());
        p.executeUpdate();
    }

    private Topic extractTopic(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Topic(id, name);
    }
}
