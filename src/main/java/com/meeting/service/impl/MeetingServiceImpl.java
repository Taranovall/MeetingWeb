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
import com.meeting.service.MeetingService;
import com.meeting.service.UserService;
import com.meeting.service.connection.ConnectionPool;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.meeting.service.connection.ConnectionPool.getInstance;
import static com.meeting.service.connection.ConnectionPool.rollback;

public class MeetingServiceImpl implements MeetingService {

    private final TopicDao topicDao;
    private final MeetingDao meetingDao;
    private final UserService userService;
    private final SpeakerDao speakerDao;

    public MeetingServiceImpl() {
        this.topicDao = new TopicDaoImpl();
        this.meetingDao = new MeetingDaoImpl();
        this.userService = new UserServiceImpl();
        this.speakerDao = new SpeakerDaoImpl();
    }


    @Override
    public void createMeeting(Meeting meeting, String[] topics, String[] speakers, File image) {
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

            meeting.setTopics(topicSpeakerMap.keySet());
            topicDao.addTopicsToMeeting(meeting.getId(), topicSpeakerMap.keySet(), c);

            for (Map.Entry<Topic, Speaker> entry : topicSpeakerMap.entrySet()) {
                speakerDao.sendInvite(entry.getValue(), meeting, entry.getKey(), c);
            }
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            rollback(c);
            image.delete();
        }
    }

    @Override
    public List<Meeting> getAllMeetings() {
        List<Meeting> meetings = null;
        try (Connection c = getInstance().getConnection()) {
            c.setAutoCommit(true);
            meetings = meetingDao.getAll(c);
        } catch (SQLException e) {
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

    /**
     * @return path to folder in which images are stored.
     */
    private String getPathToImageFolder(){
        String pathToImageFolder = null;
        String path = this.getClass().getResource("").getPath();
        String pathArr[] = path.split("/target/");
        if (2 == pathArr.length) { //pathArr[0] is webapp directory path
            pathToImageFolder = pathArr[0] + "/target/classes/images/";
        }
        return pathToImageFolder;
    }
}
