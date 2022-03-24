package com.meeting.service.impl;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.User;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.UserService;
import com.meeting.service.ValidationService;
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

    private UserService userService;

    public ValidationServiceImpl() {
        userService = new UserServiceImpl();
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
        String regex = "^.{8,}$";
        if (!password.matches(regex)) {
            request.setAttribute(MESSAGE, ErrorMessageUtil.getByLocale(request, PASSWORD_LENGTH_NOT_VALID));
            return true;
        }
        regex = "^(?=.*[a-zA-Zа-яА-я]).*$";
        if (!password.matches(regex)) {
            request.setAttribute(MESSAGE, ErrorMessageUtil.getByLocale(request, PASSWORD_NOT_CONTAIN_ANY_LETTER));
            return true;
        }
        regex = "^(?=.*[0-9]).*$";
        if (!password.matches(regex)) {
            request.setAttribute(MESSAGE, ErrorMessageUtil.getByLocale(request, PASSWORD_NOT_CONTAIN_ANY_DIGIT));
            return true;
        }
        return false;
    }

    @Override
    public boolean meetingMainInfoValidator(Meeting meeting, HttpServletRequest request) {
        String errorMessage = null;
        String regex = "^\\d{4}-\\d{2}-\\d{2}$";
        if (!meeting.getDate().matches(regex)) {
            errorMessage = ErrorMessageUtil.getByLocale(request, DATE_NOT_VALID);
        }

        regex = "^\\d{2}:\\d{2}$";
        boolean timeIsValidCheck = meeting.getTimeStart().matches(regex) && meeting.getTimeEnd().matches(regex);
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

        regex = "^.{1,32}$";
        if (!meeting.getName().matches(regex)) {
            errorMessage = ErrorMessageUtil.getByLocale(request, MEETING_NAME_IS_NOT_VALID);
        }

        // checks if data is valid only when creating meeting
        if (request.getRequestURI().equals("/moderator/create-meeting")) {
            String countOfTopics = request.getParameter(COUNT_OF_TOPICS);
            if (countOfTopics.length() > 0) {
                regex = "^\\d+$";
                if (!countOfTopics.matches(regex)) {
                    errorMessage = ErrorMessageUtil.getByLocale(request, PASSWORD_LENGTH_NOT_VALID);
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
    public boolean proposingTopicsValidator(String topicName, HttpServletRequest req) {
        String regex = "^.{1,96}$";
        if (!topicName.matches(regex)) {
            req.getSession().setAttribute(ERROR, ErrorMessageUtil.getByLocale(req, TOPIC_IS_TOO_LONG));
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
        String regex = "^[A-Za-z0-9+_-]+@(.+)$";
        if (!email.matches(regex)) {
            req.getSession().setAttribute(ERROR, ErrorMessageUtil.getByLocale(req, INVALID_EMAIL));
            return false;
        }
        return true;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
