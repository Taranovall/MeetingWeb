package com.meeting.service.impl;

import com.meeting.dao.MeetingDao;
import com.meeting.dao.SpeakerDao;
import com.meeting.dao.TopicDao;
import com.meeting.dao.impl.MeetingDaoImpl;
import com.meeting.dao.impl.SpeakerDaoImpl;
import com.meeting.dao.impl.TopicDaoImpl;
import com.meeting.email.SendEmail;
import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Speaker;
import com.meeting.entitiy.Topic;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.EmailException;
import com.meeting.exception.UserNotFoundException;
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

    private TopicDao topicDao;
    private MeetingDao meetingDao;
    private final SpeakerDao speakerDao;

    private UserService userService;
    private TopicService topicService;
    private SpeakerService speakerService;

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
            image.renameTo(new File(imagePath));
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
            log.info("Meeting {} just created", meeting.getName());
        } catch (SQLException e) {
            e.printStackTrace();
            rollback(c);
            image.delete();
            log.error("Cannot create meeting: ", e);
            throw new DataBaseException("Cannot create meeting", e);
        } finally {
            close(c);
        }
    }

    @Override
    public Meeting getMeetingById(Long id) throws DataBaseException, UserNotFoundException {
        Meeting meeting = null;
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            Optional<Meeting> optionalMeeting = meetingDao.getById(id, c);
            if (!optionalMeeting.isPresent()) throw new SQLException();
            meeting = optionalMeeting.get();
            extractMeetingInformation(meeting);
        } catch (SQLException e) {
            log.error("Meeting with ID = {} doesn't exist: ", id, e);
            throw new DataBaseException("Meeting with ID " + id + " doesn't exist", e);
        }
        return meeting;
    }

    @Override
    public boolean proposeTopic(Long meetingId, Long userSessionId, String topicName) throws DataBaseException {
        Connection c = null;
        Topic topic = new Topic();
        try {
            c = ConnectionPool.getInstance().getConnection();
            topic.setName(topicName);
            topicDao.save(topic, c);
            meetingDao.proposeTopic(meetingId, userSessionId, topic.getId(), c);
            c.commit();
        } catch (SQLException e) {
            log.error("Cannot propose topic: ", e);
            rollback(c);
            throw new DataBaseException("Cannot propose topic", e);
        } finally {
            close(c);
        }
        return true;
    }

    @Override
    public boolean acceptProposedTopic(Long topicId, Long speakerId, Long meetingId) throws DataBaseException {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            meetingDao.acceptProposedTopics(topicId, speakerId, meetingId, c);
            c.commit();
        } catch (SQLException e) {
            log.error("Cannot accept proposed topic: ", e);
            rollback(c);
            throw new DataBaseException("Cannot accept proposed topic", e);
        } finally {
            close(c);
        }
        return true;
    }

    @Override
    public boolean cancelProposedTopic(Long topicId, Long speakerId) throws DataBaseException {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            meetingDao.cancelProposedTopic(topicId, speakerId, c);
            c.commit();
        } catch (SQLException e) {
            log.error("Cannot cancel proposed topic", e);
            rollback(c);
            throw new DataBaseException("Cannot cancel proposed topics", e);
        } finally {
            close(c);
        }
        return true;
    }

    @Override
    public List<Meeting> getAllMeetings() throws DataBaseException, UserNotFoundException {
        List<Meeting> meetings = null;
        try (Connection c = getInstance().getConnection()) {
            c.setAutoCommit(true);
            meetings = meetingDao.getAll(c);
            for (Meeting meeting : meetings) extractMeetingInformation(meeting);
        } catch (SQLException e) {
            log.error("Cannot get all meetings", e);
            throw new DataBaseException("Cannot get all meetings", e);
        }
        return meetings;
    }

    @Override
    public Set<Meeting> getMeetingsSpeakerIsInvolvedIn(Long speakerId) throws DataBaseException, UserNotFoundException {
        Set<Meeting> meetings = new HashSet<>();
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            Set<Long> meetingsIdSet = meetingDao.getAllMeetingsIdSpeakerInvolvesIn(speakerId, c);
            for (Long id : meetingsIdSet) {
                meetings.add(getMeetingById(id));
            }
            c.commit();
        } catch (SQLException e) {
            log.error("Cannot get meetings in which speaker with ID = {} is involved", speakerId, e);
            rollback(c);
            throw new DataBaseException("Cannot get meetings in which speaker is involved", e);
        } finally {
            close(c);
        }
        return meetings;
    }

    @Override
    public Map<Speaker, Set<Topic>> getAcceptedTopicsMapByMeetingId(Long meetingId) throws DataBaseException {
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
            log.error("Cannot get accepted topics by meeting id {}", meetingId, e);
            throw new DataBaseException("Cannot get accepted topics by meeting id " + meetingId, e);
        }
        return acceptedTopicsBySpeaker;
    }

    @Override
    public Map<Topic, Set<Speaker>> getSentApplicationMapByMeetingId(Long meetingId) throws DataBaseException {
        Map<Topic, Set<Speaker>> sentApplicationMap = new HashMap<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            c.setAutoCommit(true);
            // key - topic ID, speaker id - value
            Map<Long, Set<Long>> sentApplicationMapWithIDs = meetingDao.getSentApplicationsByMeetingId(meetingId, c);
            for (Map.Entry<Long, Set<Long>> entry : sentApplicationMapWithIDs.entrySet()) {
                Topic topic = topicService.getById(entry.getKey());
                Set<Speaker> speakerSet = new HashSet<>();
                for (Long speakerId : entry.getValue()) {
                    speakerSet.add(speakerService.getSpeakerById(speakerId));
                }
                //entry.getValue().forEach(speakerId -> speakerSet.add(speakerService.getSpeakerById(speakerId)));
                sentApplicationMap.put(topic, speakerSet);
            }
        } catch (SQLException e) {
            log.error("Cannot get sent applications by meeting ID {}", meetingId, e);
            throw new DataBaseException("Cannot get sent applications by meeting ID ", e);
        }
        return sentApplicationMap;
    }

    @Override
    public Map<Topic, Speaker> getProposedTopicsBySpeakerByMeetingId(Long meetingId) throws DataBaseException {
        Map<Topic, Speaker> proposedTopicsBySpeaker = new HashMap<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            //topic Id - key, speaker id - value
            Map<Long, Long> proposedTopicsMap = meetingDao.getProposedTopicsBySpeakerByMeetingId(meetingId, c);
            for (Map.Entry<Long, Long> entry : proposedTopicsMap.entrySet()) {
                Topic topic = topicService.getById(entry.getKey());
                Speaker speaker = speakerService.getSpeakerById(entry.getValue());
                proposedTopicsBySpeaker.put(topic, speaker);
            }
            /* proposedTopicsMap.entrySet().forEach(entry -> {
                Topic topic = topicService.getById(entry.getKey());
                Speaker speaker = speakerService.getSpeakerById(entry.getValue());
                proposedTopicsBySpeaker.put(topic, speaker);
            });
           */
        } catch (SQLException e) {
            log.error("Cannot get proposed topics by speaker for meeting with ID: {}", meetingId, e);
            throw new DataBaseException("Cannot get proposed topics by speaker for meeting with ID: " + meetingId, e);
        }
        return proposedTopicsBySpeaker;
    }

    @Override
    public void updateInformation(Meeting meeting, Meeting meetingBeforeUpdating) throws DataBaseException, EmailException, UserNotFoundException {
        if (meeting.equals(meetingBeforeUpdating)) return;
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            meetingDao.updateInformation(meeting, c);

            List<String> listWithEmailOfParticipants = new LinkedList<>();
            //list with participants of this meeting with role 'user'
            List<User> userList = getParticipantsByMeetingId(meeting.getId());
            //set with participants of this meeting with role 'speaker'
            Set<Long> speakerIds = meetingDao.getSpeakerIdsByMeetingId(meeting.getId(), c);
            for (Long speakerId : speakerIds) {
                userList.add(userService.getUserById(speakerId));
            }
            userList.stream().filter(user -> Objects.nonNull(user.getEmail())).map(email -> listWithEmailOfParticipants.add(email.getEmail())).count();
            if (listWithEmailOfParticipants.size() > 0) {
                // create and fill array with emails
                String[] emailsArray = new String[listWithEmailOfParticipants.size()];
                listWithEmailOfParticipants.toArray(emailsArray);

                String topic = String.format("Changes in meeting '%s'", meeting.getName());
                String emailMessage = creatingEmailMessage(meeting, meetingBeforeUpdating);

                SendEmail sendEmail = new SendEmail(emailsArray, topic);
                sendEmail.sendMessage(emailMessage);
            }

            c.commit();
        } catch (SQLException e) {
            log.error("Cannot update information:", e);
            rollback(c);
            throw new DataBaseException("Cannot update information", e);
        } finally {
            close(c);
        }
    }

    private String creatingEmailMessage(Meeting meeting, Meeting meetingBeforeUpdating) {
        StringBuffer sb = new StringBuffer();
        sb.append("Meeting '").append(meeting.getName()).append("' has some changes:").append("\n");

        if (!meeting.getTimeStart().equals(meetingBeforeUpdating.getTimeStart())) {
            sb.append("\t").append("-")
                    .append("Start time has been changed from ")
                    .append(meetingBeforeUpdating.getTimeStart()).append(" to ")
                    .append(meeting.getTimeStart()).append(";")
                    .append("\n");
        }

        if (!meeting.getTimeEnd().equals(meetingBeforeUpdating.getTimeEnd())) {
            sb.append("\t").append("-")
                    .append("End time has been changed from ")
                    .append(meetingBeforeUpdating.getTimeEnd()).append(" to ")
                    .append(meeting.getTimeEnd()).append(";")
                    .append("\n");
        }

        if (!meeting.getDate().equals(meetingBeforeUpdating.getDate())) {
            sb.append("\t").append("-")
                    .append("Date of the meeting has been changed from ")
                    .append(meetingBeforeUpdating.getDate()).append(" to ")
                    .append(meeting.getDate()).append(";")
                    .append("\n");
        }

        if (!meeting.getDate().equals(meetingBeforeUpdating.getDate())) {
            sb.append("\t").append("-")
                    .append("Meeting's place has been changed from ")
                    .append(meetingBeforeUpdating.getPlace()).append(" to ")
                    .append(meeting.getPlace()).append(";")
                    .append("\n");
        }

        sb.append("Good luck!");
        return sb.toString();
    }


    @Override
    public List<User> getParticipantsByMeetingId(Long meetingId) throws DataBaseException, UserNotFoundException {
        List<User> participants = new LinkedList<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            List<Long> listWithParticipantIds = meetingDao.getParticipantsIdByMeetingId(meetingId, c);
            for (Long id : listWithParticipantIds) {
                participants.add(userService.getUserById(id));
            }
            //listWithParticipantIds.forEach(id -> participants.add(userService.getUserById(id)));
        } catch (SQLException e) {
            log.error("Cannot get participants of this meeting with ID {} by his ID", meetingId, e);
            throw new DataBaseException("Cannot get participants of this meeting with ID" + meetingId + " by his ID", e);
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
            log.error("Cannot mark present users", e);
            rollback(c);
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

    @Override
    public Set<Speaker> getSpeakersByMeetingId(Long meetingId) throws DataBaseException {
        Set<Speaker> speakers = new HashSet<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            Set<Long> speakerIds = meetingDao.getSpeakerIdsByMeetingId(meetingId, c);
            for (Long id : speakerIds) {
                speakers.add(speakerService.getSpeakerById(id));
            }
        } catch (SQLException e) {
            log.error("Cannot get speaker by meeting ID {}", meetingId, e);
            throw new DataBaseException("Cannot get speaker by meeting ID " + meetingId, e);
        }
        return speakers;
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
                                User user = null;
                                try {
                                    user = userService.getUserByLogin(speakers[i]);
                                } catch (UserNotFoundException e) {
                                    log.error("Cannot get user by login: {}", speakers[i], e);
                                }
                                return new Speaker(user.getId(), user.getLogin());
                            }
                        }));
    }

    private void extractMeetingInformation(Meeting meeting) throws DataBaseException, UserNotFoundException {
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

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setTopicDao(TopicDao topicDao) {
        this.topicDao = topicDao;
    }

    public void setMeetingDao(MeetingDao meetingDao) {
        this.meetingDao = meetingDao;
    }

    public void setTopicService(TopicService topicService) {
        this.topicService = topicService;
    }

    public void setSpeakerService(SpeakerService speakerService) {
        this.speakerService = speakerService;
    }
}
