package com.meeting.service.impl;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.TopicService;
import com.meeting.service.UserService;
import com.meeting.service.ValidationService;
import com.meeting.util.Constant;
import com.meeting.util.ErrorMessageUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.meeting.util.Constant.COUNT_OF_TOPICS;
import static com.meeting.util.Constant.COUNT_OF_TOPICS_IS_NOT_VALID;
import static com.meeting.util.Constant.COUNT_OF_TOPICS_IS_NULL;
import static com.meeting.util.Constant.DATE_AND_TIME_NOT_VALID;
import static com.meeting.util.Constant.DATE_NOT_VALID;
import static com.meeting.util.Constant.ERROR;
import static com.meeting.util.Constant.INCORRECT_PASSWORD;
import static com.meeting.util.Constant.INVALID_COUNT_OF_TOPICS;
import static com.meeting.util.Constant.INVALID_EMAIL;
import static com.meeting.util.Constant.LOGIN;
import static com.meeting.util.Constant.LOGIN_ERROR;
import static com.meeting.util.Constant.LOGIN_IS_NOT_VALID;
import static com.meeting.util.Constant.MEETING_NAME_IS_NOT_VALID;
import static com.meeting.util.Constant.MESSAGE;
import static com.meeting.util.Constant.OPTION_NOT_SELECTED;
import static com.meeting.util.Constant.PASSWORD;
import static com.meeting.util.Constant.PASSWORD_CONFIRM;
import static com.meeting.util.Constant.PASSWORD_ERROR;
import static com.meeting.util.Constant.PASSWORD_LENGTH_NOT_VALID;
import static com.meeting.util.Constant.PASSWORD_NOT_CONTAIN_ANY_DIGIT;
import static com.meeting.util.Constant.PASSWORD_NOT_CONTAIN_ANY_LETTER;
import static com.meeting.util.Constant.PASSWORD_NOT_MATCH;
import static com.meeting.util.Constant.PHOTO_NOT_CHOSEN;
import static com.meeting.util.Constant.QUERY_IS_EMPTY;
import static com.meeting.util.Constant.QUERY_IS_NOT_VALID_ATTRIBUTE_NAME;
import static com.meeting.util.Constant.QUERY_IS_TOO_LONG;
import static com.meeting.util.Constant.TIME_NOT_VALID;
import static com.meeting.util.Constant.TOPIC_IS_TOO_LONG;
import static com.meeting.util.Constant.TOPIC_NAME_NOT_UNIQUE;
import static com.meeting.util.Constant.USER_NOT_EXIST;

public class ValidationServiceImpl implements ValidationService {

    private static final Logger log = LogManager.getLogger(ValidationServiceImpl.class);
    private static final String PASSWORD_LENGTH_REGEX = "^.{8,}$";
    private static final String PASSWORD_CONTAINS_LETTERS_REGEX = "^(?=.*[a-zA-Zа-яА-я]).*$";
    private static final String PASSWORD_CONTAINS_DIGITS_REGEX = "^(?=.*[0-9]).*$";
    private static final String DATE_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";
    private static final String TIME_REGEX = "^\\d{2}:\\d{2}$";
    private static final String MEETING_NAME_LENGTH_REGEX = "^.{1,32}$";
    private static final String FIELD_CONTAINS_ONLY_DIGITS_REGEX = "^\\d+$";
    private static final String TOPIC_LENGTH_REGEX = "^.{1,96}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_-]+@(.+)$";

    private UserService userService;
    private TopicService topicService;

    public ValidationServiceImpl() {
        userService = new UserServiceImpl();
        topicService = new TopicServiceImpl();
    }

    @Override
    public User registrationValidator(HttpServletRequest req) {
        final String login = req.getParameter(LOGIN);
        final String password = req.getParameter(PASSWORD);
        final String passwordConfirm = req.getParameter(PASSWORD_CONFIRM);

        boolean validation = loginValidator(login, req);
        if (validation) {
            req.setAttribute(LOGIN_ERROR, true);
            return null;
        }
        validation = passwordValidator(password, passwordConfirm, req);
        if (validation) {
            req.setAttribute(PASSWORD_ERROR, true);
            return null;
        }
        return new User(login, password);
    }

    @Override
    public User authValidator(HttpServletRequest req) {
        final String login = req.getParameter(LOGIN);
        final String password = req.getParameter(PASSWORD);
        User userDB = null;
        try {
            userDB = userService.getUserByLogin(login);
        } catch (UserNotFoundException e) {
            log.info("Someone tried to log into account which doesn't exist");
        }

        if (userDB == null) {
            req.setAttribute(MESSAGE, ErrorMessageUtil.getByLocale(req, USER_NOT_EXIST));
            req.setAttribute(LOGIN_ERROR, true);
        } else if (!password.equals(userDB.getPassword())) {
            req.setAttribute(MESSAGE, ErrorMessageUtil.getByLocale(req, INCORRECT_PASSWORD));
            req.setAttribute(PASSWORD_ERROR, true);
        }
        return userDB;
    }

    @Override
    public boolean searchValidator(String query, HttpServletRequest req) {
        HttpSession session = req.getSession();
        int queryLength = query.trim().length();
        if (queryLength == 0) {
            session.setAttribute(QUERY_IS_NOT_VALID_ATTRIBUTE_NAME, ErrorMessageUtil.getByLocale(req, QUERY_IS_EMPTY));
            return false;
        } else if (queryLength >= 32) {
            session.setAttribute(QUERY_IS_NOT_VALID_ATTRIBUTE_NAME, ErrorMessageUtil.getByLocale(req, QUERY_IS_TOO_LONG));
            return false;
        }
        return true;
    }

    private boolean loginValidator(String login, HttpServletRequest request) {
        if (login.length() < 1 || login.length() > 16) {
            request.setAttribute(MESSAGE, ErrorMessageUtil.getByLocale(request, LOGIN_IS_NOT_VALID));
            return true;
        }
        return false;
    }

    private boolean passwordValidator(String password, String passwordConfirm, HttpServletRequest request) {
        if (!password.equals(passwordConfirm)) {
            request.setAttribute(MESSAGE, ErrorMessageUtil.getByLocale(request, PASSWORD_NOT_MATCH));
            return true;
        }
        if (!password.matches(PASSWORD_LENGTH_REGEX)) {
            request.setAttribute(MESSAGE, ErrorMessageUtil.getByLocale(request, PASSWORD_LENGTH_NOT_VALID));
            return true;
        }
        if (!password.matches(PASSWORD_CONTAINS_LETTERS_REGEX)) {
            request.setAttribute(MESSAGE, ErrorMessageUtil.getByLocale(request, PASSWORD_NOT_CONTAIN_ANY_LETTER));
            return true;
        }
        if (!password.matches(PASSWORD_CONTAINS_DIGITS_REGEX)) {
            request.setAttribute(MESSAGE, ErrorMessageUtil.getByLocale(request, PASSWORD_NOT_CONTAIN_ANY_DIGIT));
            return true;
        }
        return false;
    }

    @Override
    public boolean meetingMainInfoValidator(Meeting meeting, HttpServletRequest request) {
        String errorMessage = null;
        if (!meeting.getDate().matches(DATE_REGEX)) {
            errorMessage = ErrorMessageUtil.getByLocale(request, DATE_NOT_VALID);
        }

        boolean timeIsValidCheck = meeting.getTimeStart().matches(TIME_REGEX) && meeting.getTimeEnd().matches(TIME_REGEX);
        // return true if end time is greater than start time
        boolean correctMeetingDuration = meeting.getTimeStart().compareTo(meeting.getTimeEnd()) <= 0;
        if (!timeIsValidCheck || !correctMeetingDuration) {
            errorMessage = ErrorMessageUtil.getByLocale(request, TIME_NOT_VALID);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String meetingStartTime = String.format("%s %s", meeting.getDate(), meeting.getTimeStart());
        LocalDateTime now = LocalDateTime.now();
        String currentTime = dtf.format(now);
        if (meetingStartTime.compareTo(currentTime) <= 0) {
            errorMessage = ErrorMessageUtil.getByLocale(request, DATE_AND_TIME_NOT_VALID);
        }

        if (!meeting.getName().matches(MEETING_NAME_LENGTH_REGEX)) {
            errorMessage = ErrorMessageUtil.getByLocale(request, MEETING_NAME_IS_NOT_VALID);
        }

        // checks if data is valid only when creating meeting
        if (request.getRequestURI().equals("/moderator/create-meeting")) {
            String countOfTopics = request.getParameter(COUNT_OF_TOPICS);
            if (countOfTopics.length() > 0) {
                if (!countOfTopics.matches(FIELD_CONTAINS_ONLY_DIGITS_REGEX)) {
                    errorMessage = ErrorMessageUtil.getByLocale(request, COUNT_OF_TOPICS_IS_NOT_VALID);
                } else {
                    Integer countOfTopicsInt = Integer.parseInt(countOfTopics);
                    if (countOfTopicsInt > 10) {
                        errorMessage = ErrorMessageUtil.getByLocale(request, INVALID_COUNT_OF_TOPICS);
                    }
                }
            } else {
                errorMessage = ErrorMessageUtil.getByLocale(request, COUNT_OF_TOPICS_IS_NULL);
            }
        }

        if (Objects.nonNull(errorMessage)) {
            request.getSession().setAttribute(ERROR, errorMessage);
            return false;
        }
        return true;
    }

    @Override
    public boolean meetingPostValidator(String[] topics, Part uploadedImage, HttpServletRequest req) {
        String errorMessage = null;

        Set<String> topicSet = new HashSet<>(Arrays.asList(topics));
        if (topicSet.size() != topics.length) {
            errorMessage = ErrorMessageUtil.getByLocale(req, TOPIC_NAME_NOT_UNIQUE);
        }

        for (String topic : topics) {
            if (Objects.isNull(topic) || topic.length() < 1 || topic.length() > 96) {
                errorMessage = ErrorMessageUtil.getByLocale(req, TOPIC_IS_TOO_LONG);
                break;
            }
        }

        if (uploadedImage.getSize() <= 0) errorMessage = ErrorMessageUtil.getByLocale(req, PHOTO_NOT_CHOSEN);

        if (Objects.nonNull(errorMessage)) {
            req.setAttribute(ERROR, errorMessage);
            return false;
        }

        return true;
    }

    @Override
    public boolean proposingTopicsValidator(String topicName, Long meetingId, HttpServletRequest req) throws DataBaseException {
        if (!topicName.matches(TOPIC_LENGTH_REGEX)) {
            req.getSession().setAttribute(ERROR, ErrorMessageUtil.getByLocale(req, TOPIC_IS_TOO_LONG));
            return false;
        }
        if (topicService.isTopicExist(topicName, meetingId)) {
            req.getSession().setAttribute(ERROR, ErrorMessageUtil.getByLocale(req, Constant.TOPIC_IS_ALREADY_EXIST));
            return false;
        }
        return true;
    }

    @Override
    public boolean chooseSpeakerValidator(String speakerId, HttpServletRequest req) {
        if (speakerId.equals("none")) {
            req.getSession().setAttribute(ERROR, ErrorMessageUtil.getByLocale(req, OPTION_NOT_SELECTED));
            return false;
        }
        return true;
    }

    @Override
    public boolean emailValidator(String email, HttpServletRequest req) {
        if (!email.matches(EMAIL_REGEX)) {
            req.getSession().setAttribute(ERROR, ErrorMessageUtil.getByLocale(req, INVALID_EMAIL));
            return false;
        }
        return true;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setTopicService(TopicService topicService) {
        this.topicService = topicService;
    }
}
