package com.meeting.dao.impl;

import com.meeting.dao.MeetingDao;
import com.meeting.entitiy.Meeting;
import com.meeting.util.SQLQuery;

import java.sql.*;
import java.util.*;

import static com.meeting.util.SQLQuery.*;

public class MeetingDaoImpl implements MeetingDao {

    @Override
    public Optional<Meeting> getById(Long id, Connection c) throws SQLException {
        Optional<Meeting> optionalMeeting = Optional.empty();
        PreparedStatement p = c.prepareStatement(SQLQuery.GET_MEETING_BY_ID);
        p.setLong(1, id);
        ResultSet rs = p.executeQuery();
        if (rs.next()) {
            Meeting meeting = extractMeeting(rs, c);
            optionalMeeting = Optional.of(meeting);
        }
        return optionalMeeting;
    }

    @Override
    public List<Meeting> getAll(Connection c) throws SQLException {
        List<Meeting> meetings = new LinkedList<>();
        PreparedStatement p = c.prepareStatement(GET_ALL_MEETINGS_SQL);
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            Meeting meeting = extractMeeting(rs, c);
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

    @Override
    public Set<Long> getAllMeetingsIdSpeakerInvolvesIn(Long speakerId, Connection c) throws SQLException {
        Set<Long> meetingsIdSet = new HashSet<>();
        PreparedStatement p = c.prepareStatement(GET_ALL_MEETINGS_ID_WHERE_SPEAKER_INVOLVES_IN);
        p.setLong(1, speakerId);
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            meetingsIdSet.add(rs.getLong("meeting_id"));
        }
        return meetingsIdSet;
    }

    /**
     * Extracts meeting from result set
     */
    private Meeting extractMeeting(ResultSet rs, Connection c) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String date = rs.getString("date");
        String time = rs.getString("time");
        String place = rs.getString("place");
        String photoPath = rs.getString("photo_path");
        return new Meeting(id, name, date, time, place, photoPath);
    }

}
