package com.meeting.util;

public class SQLQuery {

    //User requests
    public static final String GET_USER_BY_ID_SQL ="SELECT * FROM users WHERE id=?";
    public static final String CREATE_USER_SQL = "INSERT INTO users (login, password) VALUES (?,?)";
    public static final String GET_ALL_USERS_SQL = "SELECT * FROM users";
    public static final String GET_USER_BY_LOGIN_SQL = "SELECT * FROM users WHERE login=?";
    public static final String ADD_ROLE_FOR_USER_SQL = "INSERT INTO user_roles VALUES (?, (SELECT id FROM roles WHERE NAME ILIKE ?))";
    public static final String GET_ALL_USER_ROLES_BY_ID_SQL = "SELECT name " +
            "FROM roles r, users u, user_roles ur " +
            "WHERE r.id=ur.role_id " +
            "AND u.id=ur.user_id " +
            "AND u.id=?";
    public static final String GET_ALL_USERS_BY_ROLE_SQL = "SELECT * FROM roles r, users u, user_roles ur WHERE r.id=ur.role_id AND u.id=ur.user_id AND r.name ILIKE ?";


    //Topic requests
    public static final String CREATE_TOPIC_SQL = "INSERT INTO topics (name) VALUES (?)";
    public static final String CREATE_FREE_TOPIC_SQL = "INSERT INTO free_topics (meeting_id, topic_id) values (?, ?)";
    public static final String GET_ALL_TOPICS_BY_MEETING_ID_SQL = "SELECT t.id, t.name from topics t, meeting_topics mt WHERE t.id = mt.topic_id AND meeting_id=?";
    public static final String LINK_TOPIC_WITH_MEETING_SQL = "INSERT INTO meeting_topics (meeting_id, topic_id) VALUES (?, ?)";

    //Meeting requests
    public static final String CREATE_MEETING_SQL = "INSERT INTO meetings (name, date, time, place, photo_path) VALUES (?, ?, ?, ?, ?)";
    public static final String GET_ALL_MEETINGS_SQL = "SELECT * FROM meetings";

    //Speaker requests
    public static final String INVITE_SPEAKER_TO_MEETING_SQL = "INSERT INTO speaker_topics (speaker_id, topic_id) VALUES (?, ?)";


}
