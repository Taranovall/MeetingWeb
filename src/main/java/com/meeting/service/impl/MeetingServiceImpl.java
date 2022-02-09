package com.meeting.service.impl;

import com.meeting.dao.MeetingDao;
import com.meeting.dao.SpeakerDao;
import com.meeting.dao.TopicDao;
import com.meeting.dao.impl.MeetingDaoImpl;
import com.meeting.dao.impl.SpeakerDaoImpl;
import com.meeting.dao.impl.TopicDaoImpl;
import com.meeting.entitiy.*;
import com.meeting.exception.DataBaseException;
import com.meeting.service.MeetingService;
import com.meeting.service.SpeakerService;
import com.meeting.service.TopicService;
import com.meeting.service.UserService;
import com.meeting.service.connection.ConnectionPool;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.meeting.service.connection.ConnectionPool.*;

public class MeetingServiceImpl implements MeetingService {

    private final TopicDao topicDao;
    private final MeetingDao meetingDao;
    private final SpeakerDao speakerDao;

    private final UserService userService;
    private final TopicService topicService;
    private final SpeakerService speakerService;

    public MeetingServiceImpl() {
        this.topicDao = new TopicDaoImpl();
        this.meetingDao = new MeetingDaoImpl();
        this.speakerDao = new SpeakerDaoImpl();
        this.userService = new UserServiceImpl();
        this.topicService = new TopicServiceImpl();
        this.speakerService = new SpeakerServiceImpl();
    }


    @Override
    public void createMeeting(User sessionUser, Meeting meeting, String[] topics, String[] speakers, File image) {
        Connection c = null;
        String imageFolderPath = this.getClass().getClassLoader().getResource("images").getPath();

        String imagePath = imageFolderPath + image.getName();
        Map<Topic, Speaker> topicSpeakerMap = mergeArrays(topics, speakers);
        try {
            c = ConnectionPool.getInstance().getConnection();
            c.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            boolean isRenamed = image.renameTo(new File(imagePath));
            meeting.setPhotoPath("/image/" + image.getName());
            meetingDao.save(meeting, c);

            meeting.setFreeTopics(topicSpeakerMap.keySet());
            topicDao.addTopicsToMeeting(meeting.getId(), topicSpeakerMap.keySet(), c);

            for (Map.Entry<Topic, Speaker> entry : topicSpeakerMap.entrySet()) {
                Speaker speaker = entry.getValue();
                Topic topic = entry.getKey();

                // creates topic and doesn't invite user to be a speaker if option wasn't selected
                if (speaker.getLogin().equals("none")) {
                    topicDao.createFreeTopic(meeting, topic, c);
                    continue;
                }

                // invites user to be a speaker therefore option with his name was selected
                speakerDao.sendInvite(speaker.getId(), topic.getId(), sessionUser.getId(), c);
            }
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            rollback(c);
            image.delete();
        }
    }

    @Override
    public Meeting getMeetingById(Long id) throws DataBaseException {
        Connection c = null;
        Meeting meeting = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            Optional<Meeting> optionalMeeting = meetingDao.getById(id, c);
            if (!optionalMeeting.isPresent()) throw new DataBaseException("Meeting doesn't exist");
            meeting = optionalMeeting.get();
            extractMeetingInformation(meeting);
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            rollback(c);
        } finally {
            close(c);
        }
        return meeting;
    }

    @Override
    public boolean proposeTopic(Long meetingId, Long userSessionId, String topicName) {
        Connection c = null;
        Topic topic = new Topic();
        try {
            c = ConnectionPool.getInstance().getConnection();
            topic.setName(topicName);
            topicDao.save(topic, c);
            meetingDao.proposeTopic(meetingId,userSessionId, topic.getId(), c);
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
    public boolean acceptProposedTopic(Long topicId, Long speakerId, Long meetingId) {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            meetingDao.acceptProposedTopics(topicId, speakerId, meetingId, c);
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
    public boolean cancelProposedTopic(Long topicId, Long speakerId) {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            meetingDao.cancelProposedTopic(topicId, speakerId, c);
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
    public List<Meeting> getAllMeetings() {
        List<Meeting> meetings = null;
        try (Connection c = getInstance().getConnection()) {
            c.setAutoCommit(true);
            meetings = meetingDao.getAll(c);
            for (Meeting meeting : meetings) extractMeetingInformation(meeting);
        } catch (SQLException | DataBaseException e) {
            e.printStackTrace();
        }
        return meetings;
    }

    @Override
    public Set<Meeting> getMeetingsSpeakerIsInvolvedIn(Long speakerId) {
        Set<Meeting> meetings = new HashSet<>();
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            Set<Long> meetingsIdSet = meetingDao.getAllMeetingsIdSpeakerInvolvesIn(speakerId, c);
            for (Long id : meetingsIdSet) {
                meetings.add(getMeetingById(id));
            }
            c.commit();
        } catch (SQLException | DataBaseException e) {
            e.printStackTrace();
            rollback(c);
        } finally {
            close(c);
        }
        return meetings;
    }

    @Override
    public Map<Speaker, Set<Topic>> getAcceptedTopicsMapByMeetingId(Long meetingId) {
        Map<Speaker, Set<Topic>> acceptedTopicsBySpeaker = new HashMap<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            c.setAutoCommit(true);
            // topic id - key, speaker id - value
            Map<Long, Long> topicSpeakerIdMap = meetingDao.getTopicSpeakerIdMapByMeetingId(meetingId, c);

            for (Map.Entry<Long, Long> entry : topicSpeakerIdMap.entrySet()) {
                Speaker speaker = speakerService.getSpeakerById(entry.getValue());
                Topic topic = topicService.getById(entry.getKey());

                Set<Topic> topicSet = acceptedTopicsBySpeaker.get(speaker);

                if (topicSet == null) {
                    topicSet = new HashSet<>();
                    topicSet.add(topic);
                    acceptedTopicsBySpeaker.put(speaker, topicSet);
                    continue;
                }
                topicSet.add(topic);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return acceptedTopicsBySpeaker;
    }

    @Override
    public Map<Topic, Set<Speaker>> getSentApplicationMapByMeetingId(Long meetingId) {
        Map<Topic, Set<Speaker>> sentApplicationMap = new HashMap<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            c.setAutoCommit(true);
            // key - topic ID, speaker id - value
            Map<Long, Set<Long>> sentApplicationMapWithIDs = meetingDao.getSentApplicationsByMeetingId(meetingId, c);
            for (Map.Entry<Long, Set<Long>> entry : sentApplicationMapWithIDs.entrySet()) {
                Topic topic = topicService.getById(entry.getKey());
                Set<Speaker> speakerSet = new HashSet<>();
                entry.getValue().forEach(speakerId -> speakerSet.add(speakerService.getSpeakerById(speakerId)));
                sentApplicationMap.put(topic, speakerSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sentApplicationMap;
    }

    @Override
    public Map<Topic, Speaker> getProposedTopicsBySpeakerByMeetingId(Long meetingId) {
        Map<Topic, Speaker> proposedTopicsBySpeaker = new HashMap<>();
        try(Connection c = ConnectionPool.getInstance().getConnection()) {
            //topic Id - key, speaker id - value
            Map<Long,Long> proposedTopicsMap = meetingDao.getProposedTopicsBySpeakerIdMap(meetingId, c);
            proposedTopicsMap.entrySet().forEach(entry -> {
                Topic topic = topicService.getById(entry.getKey());
                Speaker speaker = speakerService.getSpeakerById(entry.getValue());
                proposedTopicsBySpeaker.put(topic, speaker);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proposedTopicsBySpeaker;
    }

    /**
     * creates map in which Topic is a key and Speaker is a value
     *
     * @param topics   an array whose values will be put into object Topic that will be a key of the map
     * @param speakers an array whose values will be put into object Speakers that will be a value of the map
     */
    private Map<Topic, Speaker> mergeArrays(String[] topics, String[] speakers) {
        return IntStream.range(0, topics.length).boxed()
                .collect(Collectors.toMap(i -> new Topic(topics[i]),
                        i -> {
                            if (speakers[i].equals("none")) {
                                return new Speaker(speakers[i]);
                            } else {
                                User user = userService.getUserByLogin(speakers[i]);
                                return new Speaker(user.getId(), user.getLogin());
                            }
                        }));
    }

    private void extractMeetingInformation(Meeting meeting) throws SQLException, DataBaseException {
        Set<Topic> freeTopics = topicService.getAllFreeTopicsByMeetingId(meeting.getId());
        Map<Speaker, Set<Topic>> topicsWithSpeakers = getAcceptedTopicsMapByMeetingId(meeting.getId());
        Map<Topic, Set<Speaker>> sentApplicationMap = getSentApplicationMapByMeetingId(meeting.getId());
        Map<Topic, Speaker> proposedTopics = getProposedTopicsBySpeakerByMeetingId(meeting.getId());

        meeting.setFreeTopics(freeTopics);
        meeting.setSpeakerTopics(topicsWithSpeakers);
        meeting.setSentApplicationsMap(sentApplicationMap);
        meeting.setProposedTopicsMap(proposedTopics);
    }

    /*

    /**
     * Separates topics into those to which the speaker was invited and those to which he was not
     * removing topics that already have speaker from 'allTopics' and put it into map.
     * <p>
     * After successful execution of the method in collection 'allTopics' remains only topics
     * without speaker.
     *
     * @param allTopics set with all topics of this current meeting
     *

    private Map<Speaker, Set<Topic>> separateTopics(Set<Topic> allTopics, Meeting meeting, Connection c) throws SQLException, DataBaseException {
        Map<Speaker, Set<Topic>> speakerTopics = new HashMap<>();
        Set<Topic> topics = new HashSet<>(allTopics);
        for (Topic topic : topics) {
            Optional<Speaker> optionalSpeaker = speakerDao.getSpeakerByTopicId(topic.getId(), c);

            // if this topic doesn't have speaker, iteration is skipped
            if (!optionalSpeaker.isPresent()) continue;

            Speaker speaker = optionalSpeaker.get();

            Map<Topic, State> topicStateMap = speaker.getSpeakerTopics().get(meeting);

            if (topicStateMap.get(topic).equals(State.ACCEPTED)) {
                allTopics.remove(topic);
                Set<Topic> topicsOfCurrentSpeaker = speakerTopics.get(speaker);

                if (topicsOfCurrentSpeaker == null) {
                    Set<Topic> topicSet = new HashSet<>();
                    topicSet.add(topic);
                    speakerTopics.put(speaker, topicSet);
                } else {
                    topicsOfCurrentSpeaker.add(topic);
                }
            }

            if (topicStateMap.get(topic).equals(State.NOT_DEFINED)) {
                Set<Speaker> speakers = speakerDao.getAllSpeakerApplicationsByTopicId(topic.getId(), c);
                meeting.getSentApplicationsMap().put(topic, speakers);
            }
        }
        return speakerTopics;
    }*/
}
