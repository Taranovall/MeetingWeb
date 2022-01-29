package com.meeting.service.impl;

import com.meeting.entitiy.User;
import com.meeting.service.UserService;
import com.meeting.service.ValidationService;

import javax.servlet.http.HttpServletRequest;

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
            req.setAttribute("loginError",true);
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
            //TODO create Internationalization, replace all strings to constants
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
}
