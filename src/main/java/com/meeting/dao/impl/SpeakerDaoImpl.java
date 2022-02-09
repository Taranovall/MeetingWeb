package com.meeting.dao.impl;

import com.meeting.dao.MeetingDao;
import com.meeting.dao.SpeakerDao;
import com.meeting.dao.TopicDao;
import com.meeting.dao.UserDao;
import com.meeting.entitiy.*;
import com.meeting.exception.DataBaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.meeting.util.SQLQuery.*;

public class SpeakerDaoImpl implements SpeakerDao {

    private final UserDao userDao;
    private final MeetingDao meetingDao;
    private final TopicDao topicDao;

    public SpeakerDaoImpl() {
        this.userDao = new UserDaoImpl();
        this.meetingDao = new MeetingDaoImpl();
        this.topicDao = new TopicDaoImpl();
    }

    @Override
    public Optional<Speaker> getById(Long id, Connection c) throws SQLException, DataBaseException {
        Optional<User> user = userDao.getById(id, c);
        if (!user.isPresent() || !userDao.getUserRole(id, c).equals(Role.SPEAKER)) {
            return Optional.empty();
        }
        Speaker speaker = new Speaker();
        speaker.setId(user.get().getId());
        speaker.setLogin(user.get().getLogin());
        Map<Meeting, Map<Topic, State>> responseMap = getResponsesToInvitations(speaker.getId(), c);
        speaker.setSpeakerTopics(responseMap);
        return Optional.of(speaker);
    }

    @Override
    public boolean answerToApplication(Long speakerId, Long topicId, String SQLQuery, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(SQLQuery);
        p.setLong(1, speakerId);
        p.setLong(2, topicId);
        p.executeUpdate();
        return true;
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
    public List<String> getApplicationBySpeakerId(Long speakerId, String SQLQuery, Connection c) throws SQLException {
        List<String> applicationList = new ArrayList<>();
        PreparedStatement p = c.prepareStatement(SQLQuery);
        p.setLong(1, speakerId);
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            applicationList.add(String.valueOf(rs.getLong("topic_id")));
        }
        return applicationList;
    }

    @Override
    public Optional<Speaker> getSpeakerByTopicId(Long topicId, Connection c) throws SQLException, DataBaseException {
        Optional<Speaker> speakerOptional = Optional.empty();
        PreparedStatement p = c.prepareStatement(GET_SPEAKER_BY_TOPIC_ID);
        p.setLong(1, topicId);
        ResultSet rs = p.executeQuery();
        if (rs.next()) {
            Long speakerId = rs.getLong("speaker_id");
            speakerOptional = getById(speakerId, c);
        }
        return speakerOptional;
    }

    @Override
    public boolean sendInvite(Long speakerId, Long topicId, Long userSessionId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(INVITE_SPEAKER_TO_MEETING_SQL);
        p.setLong(1, speakerId);
        p.setLong(2, topicId);
        p.setLong(3, userSessionId);
        p.executeUpdate();
        return true;
    }

    @Override
    public boolean rollbackInvite(Long speakerId, Long topicId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(ROLLBACK_INVITE_SQL);
        p.setLong(1, speakerId);
        p.setLong(2, topicId);
        p.executeUpdate();
        return true;
    }

    @Override
    public boolean acceptApplication(Long speakerId, Long topicId, Connection c) throws SQLException {
        answerToApplication(speakerId, topicId, ACCEPT_INVITATION_SQL, c);
        PreparedStatement p = c.prepareStatement(REMOVE_REDUNDANT_APPLICATIONS_AFTER_ACCEPTING_ONE_SQL);
        p.setLong(1, speakerId);
        p.setLong(2, topicId);

        p = c.prepareStatement(REMOVE_APPLICATION_FROM_FREE_TOPICS_AFTER_ACCEPTING_IT_SQL);
        p.setLong(1, topicId);
        p.executeUpdate();
        return true;
    }

    @Override
    public Set<Speaker> getAllSpeakerApplicationsByTopicId(Long topicId, Connection c) throws SQLException, DataBaseException {
        Set<Speaker> speakers = new HashSet<>();
        Optional<Speaker> optionalSpeaker = Optional.empty();
        PreparedStatement p = c.prepareStatement(GET_ALL_SPEAKER_APPLICATIONS_BY_TOPIC_ID_SQL);
        p.setLong(1, topicId);
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            Long speakerId = rs.getLong("speaker_id");
            optionalSpeaker = getById(speakerId, c);
            optionalSpeaker.ifPresent(speakers::add);
        }
        return speakers;
    }

    /**
     * Gets from data base the user's response
     * to the invitation to become the speaker of the topic
     */
    private Map<Meeting, Map<Topic, State>> getResponsesToInvitations(Long speakerId, Connection c) throws SQLException, DataBaseException {
        PreparedStatement p = c.prepareStatement(GET_SPEAKER_RESPONSE_TO_THE_OFFER);

        Map<Meeting, Map<Topic, State>> result = new HashMap<>();
        Meeting meeting = null;

        p.setLong(1, speakerId);
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            Long topicId = rs.getLong("topic_id");
            Long meetingId = rs.getLong("meeting_id");
            Boolean invitation = rs.getBoolean("invitation");
            if (rs.wasNull()) invitation = null;
            Map<Topic, State> responseMap = extractResponse(topicId, invitation, c);
            Optional<Meeting> meetingOptional = meetingDao.getById(meetingId, c);

            if (!meetingOptional.isPresent()) {
                continue;
            }
            meeting = meetingOptional.get();

            Map<Topic, State> invitesMap = result.get(meeting);
            if (Objects.nonNull(invitesMap)) {
                invitesMap.putAll(responseMap);
                continue;
            }

            result.put(meeting, responseMap);
        }
        return result;
    }


    private Map<Topic, State> extractResponse(Long topicId, Boolean invitation, Connection c) throws SQLException, DataBaseException {
        Map<Topic, State> responseMap = new HashMap<>();

        Optional<Topic> optionalTopic = topicDao.getById(topicId, c);

        if (optionalTopic.isPresent()) {
            Topic topic = optionalTopic.get();

            if (invitation == null) {
                    responseMap.put(topic, State.NOT_DEFINED);
            } else {

                if (invitation) responseMap.put(topic, State.ACCEPTED);
                else responseMap.put(topic, State.CANCELED);
            }
        }
        return responseMap;
    }
}
