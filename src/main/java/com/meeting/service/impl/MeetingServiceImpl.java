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

    public MeetingServiceImpl() {
        this.topicDao = new TopicDaoImpl();
        this.meetingDao = new MeetingDaoImpl();
        this.speakerDao = new SpeakerDaoImpl();
        this.userService = new UserServiceImpl();
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
                    topicDao.freeTopic(meeting, topic, c);
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
            fillMeetingWithTopics(meeting, c);
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
    public List<Meeting> getAllMeetings() {
        List<Meeting> meetings = null;
        try (Connection c = getInstance().getConnection()) {
            c.setAutoCommit(true);
            meetings = meetingDao.getAll(c);
            for (Meeting meeting : meetings) fillMeetingWithTopics(meeting, c);
        } catch (SQLException | DataBaseException e) {
            e.printStackTrace();
        }
        return meetings;
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

    private void fillMeetingWithTopics(Meeting meeting, Connection c) throws SQLException, DataBaseException {
        //When the variable is initialized, all topics are stored in it
        // but after the next line, only free topics will remain here
        Set<Topic> topicsWithoutSpeakers = topicDao.getTopicsByMeetingId(meeting.getId(), c);
        Map<Speaker, Set<Topic>> topicsWithSpeakers = separateTopics(topicsWithoutSpeakers, meeting, c);
        meeting.setSpeakerTopics(topicsWithSpeakers);
        meeting.setFreeTopics(topicsWithoutSpeakers);
    }

    /**
     * Separates topics into those to which the speaker was invited and those to which he was not
     * removing topics that already have speaker from 'allTopics' and put it into map.
     * <p>
     * After successful execution of the method in collection 'allTopics' remains only topics
     * without speaker.
     *
     * @param allTopics set with all topics of this current meeting
     */
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
        }
        return speakerTopics;
    }
}
