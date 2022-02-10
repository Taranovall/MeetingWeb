package com.meeting.service.impl;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.User;
import com.meeting.service.UserService;
import com.meeting.service.ValidationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.meeting.util.Constant.QUERY_IS_NOT_VALID_ATTRIBUTE_NAME;

public class ValidationServiceImpl implements ValidationService {

    final UserService userService;

    public ValidationServiceImpl() {
        userService = new UserServiceImpl();
    }

    @Override
    public User registrationValidator(HttpServletRequest req) {
        final String login = req.getParameter("login");
        final String password = req.getParameter("password");
        final String passwordConfirm = req.getParameter("passwordConfirm");

        boolean validation = loginValidator(login, req);
        if (validation) {
            req.setAttribute("loginError", true);
            return null;
        }
        validation = passwordValidator(password, passwordConfirm, req);
        if (validation) {
            req.setAttribute("passwordError", true);
            return null;
        }
        return new User(login, password);
    }

    @Override
    public User authValidator(HttpServletRequest req) {
        final String login = req.getParameter("login");
        final String password = req.getParameter("password");
        User userDB = userService.getUserByLogin(login);

        if (userDB == null) {
            req.setAttribute("message", "Пользователя с данным логином не существует");
            req.setAttribute("loginError", true);
        } else if (!password.equals(userDB.getPassword())) {
            req.setAttribute("message", "Неверный пароль");
            req.setAttribute("passwordError", true);
        }
        return userDB;
    }

    @Override
    public boolean searchValidator(String query, HttpSession session) {
        int queryLength = query.trim().length();
        if (queryLength == 0) {
            session.setAttribute(QUERY_IS_NOT_VALID_ATTRIBUTE_NAME, "Query cannot be empty");
            return false;
        } else if (queryLength >= 32) {
            session.setAttribute(QUERY_IS_NOT_VALID_ATTRIBUTE_NAME, "Query cannot be longer than 32 characters");
            return false;
        }
        return true;
    }

    private boolean loginValidator(String login, HttpServletRequest request) {
        if (login.length() < 1 || login.length() > 16) {
            request.setAttribute("message", "Логин должен содержать от 1 до 16 символов");
            return true;
        }
        return false;
    }

    private boolean passwordValidator(String password, String passwordConfirm, HttpServletRequest request) {
        if (!password.equals(passwordConfirm)) {
            request.setAttribute("message", "Пароли не совпадают!");
            return true;
        }
        String regex = "^.{8,}$";
        if (!password.matches(regex)) {
            request.setAttribute("message", "Минимальная длина пароля 8 символов");
            return true;
        }
        regex = "^(?=.*[a-zA-Zа-яА-я]).*$";
        if (!password.matches(regex)) {
            request.setAttribute("message", "Пароль должен содержать хотя бы одну букву");
            return true;
        }
        regex = "^(?=.*[0-9]).*$";
        if (!password.matches(regex)) {
            request.setAttribute("message", "Пароль должен содержать хотя бы одну цифру");
            return true;
        }
        return false;
    }

    @Override
    public boolean createMeetingGetValidator(Meeting meeting, HttpServletRequest request) {
        String errorMessage = null;
        String regex = "^\\d{4}-\\d{2}-\\d{2}$";
        if (!meeting.getDate().matches(regex)) {
            errorMessage = "Date isn't valid";
        }

        regex = "^\\d{2}:\\d{2}$";
        if (!meeting.getTime().matches(regex)) {
            errorMessage = "Time isn't valid";
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String meetingStartTime = String.format("%s %s", meeting.getDate(), meeting.getTime());
        LocalDateTime now = LocalDateTime.now();
        String currentTime = dtf.format(now);
        if (meetingStartTime.compareTo(currentTime) <= 0) {
            errorMessage = "Incorrect date and time of the meeting";
        }

        regex = "^.{8,32}$";
        if (!meeting.getName().matches(regex)) {
            errorMessage = "Name length have to be from 8 to 32 symbols";
        }

        String countOfTopics = request.getParameter("countOfTopics");
        if (countOfTopics.length() > 0) {
            regex = "^\\d+$";
            if (!countOfTopics.matches(regex)) {
                errorMessage = "Count of topics must contain only numbers";
            } else {
                Integer countOfTopicsInt = Integer.parseInt(countOfTopics);
                if (countOfTopicsInt > 10) {
                    errorMessage = "Count of topics cannot be more that 10";
                }
            }
        } else {
            errorMessage = "Count of topics cannot be null";
        }

        if (Objects.nonNull(errorMessage)) {
            request.setAttribute("error", errorMessage);
            return false;
        }
        return true;
    }

    @Override
    public boolean createMeetingPostValidator(String[] topics, Part uploadedImage, HttpServletRequest req) {
        String errorMessage = null;

        for (String topic : topics) {
            if (Objects.isNull(topic) || topic.length() < 1 || topic.length() > 32) {
                errorMessage = "Invalid topic name";
                break;
            }
        }

        if (uploadedImage.getSize() <= 0) errorMessage = "Photo hasn't been chosen";

        if (Objects.nonNull(errorMessage)) {
            req.setAttribute("error", errorMessage);
            return false;
        }

        return true;
    }
}
