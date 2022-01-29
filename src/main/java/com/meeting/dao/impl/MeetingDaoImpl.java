package com.meeting.dao.impl;

import com.meeting.dao.MeetingDao;
import com.meeting.dao.TopicDao;
import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Topic;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.meeting.util.SQLQuery.CREATE_MEETING_SQL;
import static com.meeting.util.SQLQuery.GET_ALL_MEETINGS_SQL;

public class MeetingDaoImpl implements MeetingDao {

    private final TopicDao topicDao;

    public MeetingDaoImpl() {
        this.topicDao = new TopicDaoImpl();
    }

    @Override
    public Optional<Meeting> getById(long id, Connection c) throws SQLException {
        return Optional.empty();
    }

    @Override
    public List<Meeting> getAll(Connection c) throws SQLException {
        List<Meeting> meetings = new LinkedList<>();
        PreparedStatement p = c.prepareStatement(GET_ALL_MEETINGS_SQL);
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            Meeting meeting = extractMeeting(rs);
            Set<Topic> meetingTopics = topicDao.getTopicsByMeetingId(meeting.getId(), c);
            meeting.getTopics().addAll(meetingTopics);
            meetings.add(meeting);
        }
        return meetings;
    }

    @Override
    public void save(Meeting meeting, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(CREATE_MEETING_SQL, Statement.RETURN_GENERATED_KEYS);

        String meetingName = meeting.getName();
        String meetingDate = meeting.getDate();
        String meetingTime = meeting.getTime();
        String meetingPlace = meeting.getPlace();
        String meetingPhotoPath = meeting.getPhotoPath();

        p.setString(1, meetingName);
        p.setString(2, meetingDate);
        p.setString(3, meetingTime);
        p.setString(4, meetingPlace);
        p.setString(5, meetingPhotoPath);

        if (p.executeUpdate() > 0) {
            ResultSet rs = p.getGeneratedKeys();
            if (rs.next()) {
                Long meetingId = rs.getLong(1);
                meeting.setId(meetingId);
            }
        }
    }

    @Override
    public void delete(Meeting meeting, Connection c) {

    }

    private Meeting extractMeeting(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String date = rs.getString("date");
        String time = rs.getString("time");
        String place = rs.getString("place");
        String photoPath = rs.getString("photo_path");
        return new Meeting(id,name, date, time, place, photoPath);
    }
}
