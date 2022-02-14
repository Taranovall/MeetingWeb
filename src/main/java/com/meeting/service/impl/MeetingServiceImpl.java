package com.meeting.service.impl;

import com.meeting.dao.MeetingDao;
import com.meeting.dao.SpeakerDao;
import com.meeting.dao.TopicDao;
import com.meeting.dao.impl.MeetingDaoImpl;
import com.meeting.dao.impl.SpeakerDaoImpl;
import com.meeting.dao.impl.TopicDaoImpl;
import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Speaker;
import com.meeting.entitiy.Topic;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.service.MeetingService;
import com.meeting.service.SpeakerService;
import com.meeting.service.TopicService;
import com.meeting.service.UserService;
import com.meeting.service.connection.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.meeting.service.connection.ConnectionPool.*;

public class MeetingServiceImpl implements MeetingService {

    private static final Logger log = LogManager.getLogger(MeetingServiceImpl.class);

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
    public void createMeeting(User sessionUser, Meeting meeting, String[] topics, String[] speakers, File image) throws DataBaseException {
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

                // invites user to be a speaker therefore option with his name was selected
                if (!speaker.getLogin().equals("none")) {
                    speakerDao.sendInvite(speaker.getId(), topic.getId(), sessionUser.getId(), c);
                }
                // creates free topic
                topicDao.createFreeTopic(meeting, topic, c);
            }
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            rollback(c);
            image.delete();
            log.error("Cannot create meeting: ", e);
            throw new DataBaseException("Cannot create meeting", e);
        }
    }

    @Override
    public Meeting getMeetingById(Long id) throws DataBaseException {
        Meeting meeting = null;
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            Optional<Meeting> optionalMeeting = meetingDao.getById(id, c);
            if (!optionalMeeting.isPresent()) throw new DataBaseException("Meeting doesn't exist");
            meeting = optionalMeeting.get();
            extractMeetingInformation(meeting);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Meeting with ID = {} doesn't exist: ", id, e);
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
            meetingDao.proposeTopic(meetingId, userSessionId, topic.getId(), c);
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            rollback(c);
            log.error("Cannot propose topic: ", e);
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
            log.error("Cannot accept proposed topic: ", e);
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
            log.error("Cannot cancel proposed topic", e);
        } finally {
            close(c);
        }
        return true;
    }

    @Override
    public List<Meeting> getAllMeetings() throws DataBaseException {
        List<Meeting> meetings = null;
        try (Connection c = getInstance().getConnection()) {
            c.setAutoCommit(true);
            meetings = meetingDao.getAll(c);
            for (Meeting meeting : meetings) extractMeetingInformation(meeting);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Cannot get all meetings", e);
            throw new DataBaseException("Cannot get all meetings", e);
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
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            //topic Id - key, speaker id - value
            Map<Long, Long> proposedTopicsMap = meetingDao.getProposedTopicsBySpeakerByMeetingId(meetingId, c);
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

    @Override
    public void updateInformation(Meeting meeting) throws DataBaseException {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            meetingDao.updateInformation(meeting, c);
            c.commit();
        } catch (SQLException e) {
            log.error("Cannot update information:", e);
            rollback(c);
            throw new DataBaseException("Cannot update information", e);
        } finally {
            close(c);
        }
    }

    @Override
    public List<User> getParticipantsByMeetingId(Long meetingId) {
        List<User> participants = new LinkedList<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            List<Long> listWithParticipantIds = meetingDao.getParticipantsIdByMeetingId(meetingId, c);
            listWithParticipantIds.forEach(id -> participants.add(userService.getUserById(id)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    @Override
    public void markPresentUsers(String[] presentUsers, Long meetingId) throws DataBaseException {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            meetingDao.markPresentUsers(presentUsers, meetingId, c);
            c.commit();
        } catch (SQLException e) {
            rollback(c);
            log.error("Cannot mark present users", e);
            throw new DataBaseException("Cannot mark present users", e);
        } finally {
            close(c);
        }
    }

    @Override
    public List<Long> getPresentUserIds(Long meetingId) throws DataBaseException {
        List<Long> presentUserIds = new LinkedList<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            presentUserIds.addAll(meetingDao.getPresentUserIds(meetingId, c));
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Cannot get present users", e);
            throw new DataBaseException("Cannot get present users", e);
        }
        return presentUserIds;
    }

    @Override
    public double getAttendancePercentageByMeetingId(Long meetingId) throws DataBaseException {
        double res = 0;
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            res = meetingDao.getAttendancePercentageByMeetingId(meetingId, c);
        } catch (SQLException e) {
            log.error("Cannot calculate percentage", e);
            throw new DataBaseException("Cannot calculate percentage", e);
        }
        return res;
    }

    @Override
    public boolean isMeetingStarted(Meeting meeting) {
        boolean isStarted = false;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String meetingStartTime = String.format("%s %s", meeting.getDate(), meeting.getTimeStart());
        String currentTime = dtf.format(now);
        if (meetingStartTime.compareTo(currentTime) <= 0) isStarted = true;
        return isStarted;
    }

    @Override
    public boolean isMeetingGoingOnNow(Meeting meeting) {
        boolean isStarted = isMeetingStarted(meeting);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String meetingEndTime = String.format("%s %s", meeting.getDate(), meeting.getTimeEnd());
        String currentTime = dtf.format(now);
        // is true only if current time less than end time of the meeting
        boolean meetingIsNotEnded = meetingEndTime.compareTo(currentTime) >= 0;
        return isStarted && meetingIsNotEnded;
    }

    @Override
    public boolean isMeetingPassed(Meeting meeting) {
        return isMeetingStarted(meeting) && !isMeetingGoingOnNow(meeting);
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
        List<User> participants = getParticipantsByMeetingId(meeting.getId());
        double percentageAttendance = getAttendancePercentageByMeetingId(meeting.getId());

        meeting.setFreeTopics(freeTopics);
        meeting.setSpeakerTopics(topicsWithSpeakers);
        meeting.setSentApplicationsMap(sentApplicationMap);
        meeting.setProposedTopicsMap(proposedTopics);
        meeting.setParticipants(participants);
        meeting.setPercentageAttendance(percentageAttendance);
        meeting.setStarted(isMeetingStarted(meeting));
        meeting.setGoingOnNow(isMeetingGoingOnNow(meeting));
        meeting.setPassed(isMeetingPassed(meeting));
    }
}
