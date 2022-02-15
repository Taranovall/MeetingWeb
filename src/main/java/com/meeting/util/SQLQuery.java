package com.meeting.util;

public class SQLQuery {

    //User requests
    public static final String GET_USER_BY_ID_SQL ="SELECT * FROM users WHERE id=?";
    public static final String CREATE_USER_SQL = "INSERT INTO users (login, password, registration_date) VALUES (?,?,?)";
    public static final String GET_ALL_USERS_SQL = "SELECT * FROM users";
    public static final String GET_USER_BY_LOGIN_SQL = "SELECT * FROM users WHERE login=?";
    public static final String ADD_ROLE_FOR_USER_SQL = "INSERT INTO user_roles VALUES (?, (SELECT id FROM roles WHERE NAME ILIKE ?))";
    public static final String GET_USER_ROLE_BY_ID_SQL = "SELECT name " +
            "FROM roles r, users u, user_roles ur " +
            "WHERE r.id=ur.role_id " +
            "AND u.id=ur.user_id " +
            "AND u.id=?";
    public static final String GET_ALL_USERS_BY_ROLE_SQL = "SELECT * FROM roles r, users u, user_roles ur WHERE r.id=ur.role_id AND u.id=ur.user_id AND r.name ILIKE ?";
    public static final String USER_PARTICIPATE_SQL = "INSERT INTO meeting_participants (meeting_id, user_id) VALUES (?, ?)";
    public static final String GET_MEETING_IDS_IN_WHICH_USER_TAKES_PARK_SQL = "SELECT meeting_id FROM meeting_participants WHERE user_id = ?";
    public static final String USER_STOP_PARTICIPATING_SQL = "DELETE FROM meeting_participants WHERE meeting_id = ? AND user_id = ?";
    public static final String GET_ALL_PARTICIPANTS_BY_MEETING_ID_SQL = "SELECT user_id FROM meeting_participants WHERE meeting_id = ?";
    public static final String SET_PRESENCE_FALSE_SQL = "UPDATE meeting_participants SET is_present = false WHERE meeting_id = ?";
    public static final String MARK_PRESENT_USERS_SQL = "UPDATE meeting_participants SET is_present = true WHERE meeting_id = ? AND user_id = ?";
    public static final String GET_PRESENT_USERS_SQL = "SELECT user_id FROM meeting_participants WHERE is_present is true AND meeting_id = ?";
    public static final String GET_ATTENDED_PARTICIPANTS_SQL = "SELECT count(*) FROM meeting_participants WHERE is_present is true AND meeting_id = ?";
    public static final String SET_EMAIL_FOR_USER_SQL = "UPDATE users SET email = ? WHERE id = ?";

    //Topic requests
    public static final String CREATE_TOPIC_SQL = "INSERT INTO topics (name) VALUES (?)";
    public static final String CREATE_FREE_TOPIC_SQL = "INSERT INTO free_topics (meeting_id, topic_id) values (?, ?)";
    public static final String GET_ALL_TOPICS_BY_MEETING_ID_SQL = "SELECT t.id, t.name from topics t, meeting_topics mt WHERE t.id = mt.topic_id AND meeting_id=?";
    public static final String LINK_TOPIC_WITH_MEETING_SQL = "INSERT INTO meeting_topics (meeting_id, topic_id) VALUES (?, ?)";
    public static final String GET_TOPIC_BY_ID_SQL = "SELECT * FROM topics WHERE id = ?";
    public static final String GET_ALL_ACCEPTED_TOPICS_BY_MEETING_ID = "SELECT st.speaker_id, st.topic_id FROM speaker_topics st, meeting_topics mt WHERE st.invitation is true AND st.topic_id = mt.topic_id AND meeting_id = ?";
    public static final String GET_ALL_FREE_TOPICS_BY_MEETING_ID = "SELECT topic_id FROM free_topics WHERE meeting_id = ?";
    public static final String GET_SENT_APPLICATION_BY_MEETING_ID = "SELECT st.topic_id, st.speaker_id FROM speaker_topics st, meeting_topics mt WHERE st.invitation is null AND st.topic_id = mt.topic_id AND st.speaker_id = st.invited_by AND mt.meeting_id = ?";
    public static final String REMOVE_APPLICATION_FROM_FREE_TOPICS_AFTER_ACCEPTING_IT_SQL = "DELETE FROM free_topics WHERE topic_id = ?";
    public static final String PROPOSE_TOPIC_SQL = "INSERT INTO proposed_topics (proposed_by_speaker, meeting_id, topic_id) VALUES (?, ?, ?)";
    public static final String ACCEPT_PROPOSED_TOPIC_SQL = "INSERT INTO speaker_topics (speaker_id, topic_id, invitation, invited_by) VALUES (?, ?, true, ?)";
    public static final String REMOVE_PROPOSED_TOPIC_SQL = "DELETE FROM proposed_topics WHERE proposed_by_speaker = ? AND topic_id = ?";

    //Meeting requests
    public static final String CREATE_MEETING_SQL = "INSERT INTO meetings (name, date, time_start, time_end, place, photo_path) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String GET_ALL_MEETINGS_SQL = "SELECT * FROM meetings";
    public static final String GET_MEETING_BY_ID = "SELECT * FROM meetings WHERE id = ?";
    public static final String GET_ALL_MEETINGS_ID_WHERE_SPEAKER_INVOLVES_IN = "SELECT DISTINCT meeting_id FROM meeting_topics mt, speaker_topics st WHERE mt.topic_id = st.topic_id AND st.invitation is true AND speaker_id = ?";
    public static final String GET_PROPOSED_TOPICS_BY_MEETING_ID = "SELECT proposed_by_speaker, topic_id FROM proposed_topics WHERE meeting_id = ?";
    public static final String UPDATE_MEETING_INFORMATION_SQL = "UPDATE meetings SET time_start=?, time_end=?, date=?, place=? WHERE id = ?";

    //Speaker requests
    public static final String INVITE_SPEAKER_TO_MEETING_SQL = "INSERT INTO speaker_topics (speaker_id, topic_id, invited_by) VALUES (?, ?, ?)";
    public static final String GET_SPEAKER_BY_TOPIC_ID = "SELECT speaker_id FROM speaker_topics WHERE topic_id = ? AND invitation is not false";
    public static final String GET_SPEAKER_RESPONSE_TO_THE_OFFER = "SELECT st.topic_id, invitation, mt.meeting_id FROM speaker_topics st, meeting_topics mt WHERE st.topic_id = mt.topic_id AND speaker_id = ?";
    public static final String ROLLBACK_INVITE_SQL = "DELETE FROM speaker_topics WHERE speaker_id = ? AND topic_id = ?";
    public static final String GET_SENT_APPLICATIONS_BY_SPEAKER_ID_SQL = "SELECT topic_id FROM speaker_topics WHERE speaker_id = invited_by AND speaker_id = ?";
    public static final String GET_RECEIVED_APPLICATIONS_BY_SPEAKER_ID_SQL = "SELECT topic_id FROM speaker_topics WHERE speaker_id != invited_by AND speaker_id = ?";
    public static final String ACCEPT_INVITATION_SQL = "UPDATE speaker_topics SET invitation = true WHERE  speaker_id = ? AND topic_id = ?";
    public static final String REMOVE_REDUNDANT_APPLICATIONS_AFTER_ACCEPTING_ONE_SQL = "DELETE FROM speaker_topics WHERE invitation is null AND speaker_id != ? AND topic_id = ?";
    public static final String GET_ALL_SPEAKER_APPLICATIONS_BY_TOPIC_ID_SQL = "SELECT speaker_id FROM speaker_topics WHERE speaker_id = invited_by AND topic_id = ?";
    public static final String GET_ALL_SPEAKER_BY_MEETING_ID_SQL = "SELECT st.speaker_id FROM speaker_topics st, meeting_topics mt WHERE st.invitation is true AND st.topic_id = mt.topic_id AND meeting_id = ?";
}
