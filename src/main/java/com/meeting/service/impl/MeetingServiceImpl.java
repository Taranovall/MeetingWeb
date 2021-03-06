package com.meeting.service.impl;

import com.meeting.connection.ConnectionPool;
import com.meeting.dao.MeetingDao;
import com.meeting.dao.SpeakerDao;
import com.meeting.dao.TopicDao;
import com.meeting.dao.impl.MeetingDaoImpl;
import com.meeting.dao.impl.SpeakerDaoImpl;
import com.meeting.dao.impl.TopicDaoImpl;
import com.meeting.email.EmailSender;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.meeting.connection.ConnectionPool.close;
import static com.meeting.connection.ConnectionPool.getInstance;
import static com.meeting.connection.ConnectionPool.rollback;
import static com.meeting.util.Constant.CANNOT_ACCEPT_PROPOSED_TOPIC;
import static com.meeting.util.Constant.CANNOT_CALCULATE_PERCENTAGE;
import static com.meeting.util.Constant.CANNOT_CANCEL_PROPOSED_TOPICS;
import static com.meeting.util.Constant.CANNOT_CREATE_MEETING;
import static com.meeting.util.Constant.CANNOT_GET_ACCEPTED_TOPICS_BY_MEETING_ID;
import static com.meeting.util.Constant.CANNOT_GET_ALL_MEETINGS;
import static com.meeting.util.Constant.CANNOT_GET_MEETINGS_IN_WHICH_SPEAKER_IS_INVOLVED;
import static com.meeting.util.Constant.CANNOT_GET_PARTICIPANTS_OF_THIS_MEETING_BY_HIS_ID;
import static com.meeting.util.Constant.CANNOT_GET_PRESENT_USERS;
import static com.meeting.util.Constant.CANNOT_GET_PROPOSED_TOPICS_BY_SPEAKER_FOR_MEETING_WITH_ID;
import static com.meeting.util.Constant.CANNOT_GET_SENT_APPLICATIONS_BY_MEETING_ID;
import static com.meeting.util.Constant.CANNOT_GET_SPEAKER_BY_MEETING_ID;
import static com.meeting.util.Constant.CANNOT_GET_USER_BY_LOGIN;
import static com.meeting.util.Constant.CANNOT_MARK_PRESENT_USERS;
import static com.meeting.util.Constant.CANNOT_PROPOSE_TOPIC;
import static com.meeting.util.Constant.CANNOT_UPDATE_INFORMATION;
import static com.meeting.util.Constant.MEETING_WITH_THIS_ID_NOT_EXIST;

public class MeetingServiceImpl implements MeetingService {

    private static final Logger log = LogManager.getLogger(MeetingServiceImpl.class);

    private TopicDao topicDao;
    private MeetingDao meetingDao;
    private SpeakerDao speakerDao;

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
            log.error(CANNOT_CREATE_MEETING, e);
            throw new DataBaseException(CANNOT_CREATE_MEETING, e);
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
            log.error(MEETING_WITH_THIS_ID_NOT_EXIST + id, e);
            throw new DataBaseException(MEETING_WITH_THIS_ID_NOT_EXIST + id, e);
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
            log.error(CANNOT_PROPOSE_TOPIC, e);
            rollback(c);
            throw new DataBaseException(CANNOT_PROPOSE_TOPIC, e);
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
            log.error(CANNOT_ACCEPT_PROPOSED_TOPIC, e);
            rollback(c);
            throw new DataBaseException(CANNOT_ACCEPT_PROPOSED_TOPIC, e);
        } finally {
            close(c);
        }
        return true;
    }

    @Override
    public boolean cancelProposedTopic(Long topicId) throws DataBaseException {
        Connection c = null;
        try {
            c = ConnectionPool.getInstance().getConnection();
            meetingDao.cancelProposedTopic(topicId, c);
            c.commit();
        } catch (SQLException e) {
            log.error(CANNOT_CANCEL_PROPOSED_TOPICS, e);
            rollback(c);
            throw new DataBaseException(CANNOT_CANCEL_PROPOSED_TOPICS, e);
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
            log.error(CANNOT_GET_ALL_MEETINGS, e);
            throw new DataBaseException(CANNOT_GET_ALL_MEETINGS, e);
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
            throw new DataBaseException(CANNOT_GET_MEETINGS_IN_WHICH_SPEAKER_IS_INVOLVED, e);
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
            log.error(CANNOT_GET_ACCEPTED_TOPICS_BY_MEETING_ID + meetingId, e);
            throw new DataBaseException(CANNOT_GET_ACCEPTED_TOPICS_BY_MEETING_ID + meetingId, e);
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
            log.error(CANNOT_GET_SENT_APPLICATIONS_BY_MEETING_ID + meetingId, e);
            throw new DataBaseException(CANNOT_GET_SENT_APPLICATIONS_BY_MEETING_ID, e);
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
        } catch (SQLException e) {
            log.error(CANNOT_GET_PROPOSED_TOPICS_BY_SPEAKER_FOR_MEETING_WITH_ID + meetingId, e);
            throw new DataBaseException(CANNOT_GET_PROPOSED_TOPICS_BY_SPEAKER_FOR_MEETING_WITH_ID + meetingId, e);
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

                EmailSender emailSender = new EmailSender(emailsArray, topic);
                emailSender.sendMessage(emailMessage);
            }

            c.commit();
        } catch (SQLException e) {
            log.error(CANNOT_UPDATE_INFORMATION, e);
            rollback(c);
            throw new DataBaseException(CANNOT_UPDATE_INFORMATION, e);
        } finally {
            close(c);
        }
    }

    private String creatingEmailMessage(Meeting meeting, Meeting meetingBeforeUpdating) {
        StringBuffer sb = new StringBuffer();
        sb.append("Meeting '").append(meeting.getName()).append("' has some changes:").append("\n");
        messageAppend(sb, meetingBeforeUpdating.getTimeStart(), meeting.getTimeStart(), "Start time");
        messageAppend(sb, meetingBeforeUpdating.getTimeEnd(), meeting.getTimeEnd(), "End time");
        messageAppend(sb, meetingBeforeUpdating.getDate(), meeting.getDate(), "Date of the meeting");
        messageAppend(sb, meetingBeforeUpdating.getPlace(), meeting.getPlace(), "Meeting's place");
        sb.append("Good luck!");
        return sb.toString();
    }

    private void messageAppend(StringBuffer sb, String oldData, String newData, String fieldName) {
        if (!newData.equals(oldData)) {
            sb.append("\t").append("-")
                    .append(fieldName)
                    .append(" ").append("has been changed from").append(" ")
                    .append(oldData).append(" to ")
                    .append(newData).append(";")
                    .append("\n");
        }
    }


    @Override
    public List<User> getParticipantsByMeetingId(Long meetingId) throws DataBaseException, UserNotFoundException {
        List<User> participants = new LinkedList<>();
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            List<Long> listWithParticipantIds = meetingDao.getParticipantsIdByMeetingId(meetingId, c);
            for (Long id : listWithParticipantIds) {
                participants.add(userService.getUserById(id));
            }
        } catch (SQLException e) {
            log.error(CANNOT_GET_PARTICIPANTS_OF_THIS_MEETING_BY_HIS_ID + meetingId, e);
            throw new DataBaseException(CANNOT_GET_PARTICIPANTS_OF_THIS_MEETING_BY_HIS_ID, e);
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
            log.error(CANNOT_MARK_PRESENT_USERS, e);
            rollback(c);
            throw new DataBaseException(CANNOT_MARK_PRESENT_USERS, e);
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
            log.error(CANNOT_GET_PRESENT_USERS, e);
            throw new DataBaseException(CANNOT_GET_PRESENT_USERS, e);
        }
        return presentUserIds;
    }

    @Override
    public double getAttendancePercentageByMeetingId(Long meetingId) throws DataBaseException {
        double res = 0;
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            res = meetingDao.getAttendancePercentageByMeetingId(meetingId, c);
        } catch (SQLException e) {
            log.error(CANNOT_CALCULATE_PERCENTAGE, e);
            throw new DataBaseException(CANNOT_CALCULATE_PERCENTAGE, e);
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
            log.error(CANNOT_GET_SPEAKER_BY_MEETING_ID + meetingId, e);
            throw new DataBaseException(CANNOT_GET_SPEAKER_BY_MEETING_ID + meetingId, e);
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
                                    log.error(CANNOT_GET_USER_BY_LOGIN + speakers[i], e);
                                }
                                return new Speaker(user.getId(), user.getLogin());
                            }
                        }));
    }

    private void extractMeetingInformation(Meeting meeting) throws DataBaseException, UserNotFoundException {
        meeting.setFreeTopics(topicService.getAllFreeTopicsByMeetingId(meeting.getId()));
        meeting.setSpeakerTopics(getAcceptedTopicsMapByMeetingId(meeting.getId()));
        meeting.setSentApplicationsMap(getSentApplicationMapByMeetingId(meeting.getId()));
        meeting.setProposedTopicsMap(getProposedTopicsBySpeakerByMeetingId(meeting.getId()));
        meeting.setParticipants(getParticipantsByMeetingId(meeting.getId()));
        meeting.setPercentageAttendance(getAttendancePercentageByMeetingId(meeting.getId()));
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
