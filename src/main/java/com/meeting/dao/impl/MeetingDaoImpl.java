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
        String meetingStartTime = meeting.getTimeStart();
        String meetingEndTime = meeting.getTimeEnd();
        String meetingPlace = meeting.getPlace();
        String meetingPhotoPath = meeting.getPhotoPath();

        int k = 1;
        p.setString(k++, meetingName);
        p.setString(k++, meetingDate);
        p.setString(k++, meetingStartTime);
        p.setString(k++, meetingEndTime);
        p.setString(k++, meetingPlace);
        p.setString(k++, meetingPhotoPath);

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
    public Map<Long, Long> getTopicSpeakerIdMapByMeetingId(Long meetingId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(GET_ALL_ACCEPTED_TOPICS_BY_MEETING_ID);
        p.setLong(1, meetingId);
        ResultSet rs = p.executeQuery();
        Map<Long, Long> topicSpeakerIdMap = new HashMap<>();
        while (rs.next()) {
            Long speakerId = rs.getLong("speaker_id");
            Long topicId = rs.getLong("topic_id");
            topicSpeakerIdMap.put(topicId, speakerId);
        }
        return topicSpeakerIdMap;
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

    @Override
    public Map<Long, Set<Long>> getSentApplicationsByMeetingId(Long meetingId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(GET_SENT_APPLICATION_BY_MEETING_ID);
        p.setLong(1, meetingId);
        ResultSet rs = p.executeQuery();
        Map<Long, Set<Long>> applicationMap = new HashMap<>();
        while (rs.next()) {
            Long speakerId = rs.getLong("speaker_id");
            Long topicId = rs.getLong("topic_id");

            Set<Long> speakerIdSet = applicationMap.get(topicId);

            if (speakerIdSet == null) {
                speakerIdSet = new HashSet<>();
                speakerIdSet.add(speakerId);
                applicationMap.put(topicId, speakerIdSet);
                continue;
            }
            speakerIdSet.add(speakerId);
        }
        return applicationMap;
    }

    @Override
    public boolean proposeTopic(Long meetingId, Long userSessionId, Long topicId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(PROPOSE_TOPIC_SQL);
        p.setLong(1, userSessionId);
        p.setLong(2, meetingId);
        p.setLong(3, topicId);
        p.executeUpdate();
        return true;
    }

    @Override
    public Map<Long, Long> getProposedTopicsBySpeakerByMeetingId(Long meetingId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(GET_PROPOSED_TOPICS_BY_MEETING_ID);
        p.setLong(1, meetingId);
        ResultSet rs = p.executeQuery();
        Map<Long, Long> proposedTopicsMap = new HashMap<>();
        while (rs.next()) {
            Long topicId = rs.getLong("topic_id");
            Long speakerId = rs.getLong("proposed_by_speaker");
            proposedTopicsMap.put(topicId, speakerId);
        }
        return proposedTopicsMap;
    }

    @Override
    public boolean acceptProposedTopics(Long topicId, Long speakerId, Long meetingId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(ACCEPT_PROPOSED_TOPIC_SQL);
        p.setLong(1, speakerId);
        p.setLong(2, topicId);
        p.setLong(3, speakerId);
        p.executeUpdate();

        p = c.prepareStatement(LINK_TOPIC_WITH_MEETING_SQL);
        p.setLong(1, meetingId);
        p.setLong(2, topicId);
        p.executeUpdate();

        p = c.prepareStatement(REMOVE_PROPOSED_TOPIC_SQL);
        p.setLong(1, speakerId);
        p.setLong(2, topicId);
        p.executeUpdate();
        return true;
    }

    @Override
    public boolean cancelProposedTopic(Long topicId, Long speakerId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(REMOVE_PROPOSED_TOPIC_SQL);
        p.setLong(1, speakerId);
        p.setLong(2, topicId);
        p.executeUpdate();
        return true;
    }

    @Override
    public void updateInformation(Meeting meeting, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(SQLQuery.UPDATE_MEETING_INFORMATION_SQL);
        int k = 1;
        p.setString(k++, meeting.getTimeStart());
        p.setString(k++, meeting.getTimeEnd());
        p.setString(k++, meeting.getDate());
        p.setString(k++, meeting.getPlace());
        p.setLong(k++, meeting.getId());
        p.executeUpdate();
    }

    @Override
    public List<Long> getParticipantsIdByMeetingId(Long meetingId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(SQLQuery.GET_ALL_PARTICIPANTS_BY_MEETING_ID_SQL);
        p.setLong(1, meetingId);
        List<Long> participantsList = new LinkedList<>();
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            Long participantId = rs.getLong(1);
            participantsList.add(participantId);
        }
        return participantsList;
    }

    @Override
    public void markPresentUsers(String[] presentUsers, Long meetingId, Connection c) throws SQLException {
        //set is present in table meeting participants in case if user was presence but left meeting
        PreparedStatement p = c.prepareStatement(SQLQuery.SET_PRESENCE_FALSE_SQL);
        p.setLong(1, meetingId);
        p.executeUpdate();
        if (presentUsers != null) {
            p = c.prepareStatement(SQLQuery.MARK_PRESENT_USERS_SQL);
            p.setLong(1, meetingId);
            for (String id : presentUsers) {
                long convertedId = Long.parseLong(id);
                p.setLong(2, convertedId);
                p.executeUpdate();
            }
        }
    }

    @Override
    public List<Long> getPresentUserIds(Long meetingId, Connection c) throws SQLException {
        PreparedStatement p = c.prepareStatement(SQLQuery.GET_PRESENT_USERS_SQL);
        p.setLong(1, meetingId);
        List<Long> userIds = new LinkedList<>();
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            userIds.add(rs.getLong(1));
        }
        return userIds;
    }

    @Override
    public double getAttendancePercentageByMeetingId(Long meetingId, Connection c) throws SQLException {
        int attendedParticipants = 0;
        int participantsCount = getParticipantsIdByMeetingId(meetingId, c).size();
        PreparedStatement p = c.prepareStatement(SQLQuery.GET_ATTENDED_PARTICIPANTS_SQL);
        p.setLong(1, meetingId);
        ResultSet rs = p.executeQuery();
        while (rs.next()) {
            attendedParticipants = rs.getInt(1);
        }
        if (attendedParticipants == 0 || participantsCount == 0) return 0;
        double percentage = (double) attendedParticipants / participantsCount * 100;
        String formattedValue = String.format("%.1f", percentage).replace(",", ".");
        return Double.parseDouble(formattedValue);
    }

    /**
     * Extracts meeting from result set
     */
    private Meeting extractMeeting(ResultSet rs, Connection c) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String date = rs.getString("date");
        String timeStart = rs.getString("time_start");
        String timeEnd = rs.getString("time_end");
        String place = rs.getString("place");
        String photoPath = rs.getString("photo_path");
        return new Meeting(id, name, date, timeStart, timeEnd, place, photoPath);
    }

}
