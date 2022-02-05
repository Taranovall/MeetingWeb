package com.meeting.controller;

import com.meeting.entitiy.User;
import com.meeting.service.UserService;
import com.meeting.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static com.meeting.util.Constant.PATH_TO_PERSONAL_ACCOUNT_JSP;

@WebServlet(name = "personalAccount", urlPatterns = "/account/*")
public class PersonalAccountController extends HttpServlet {

    private final UserService userService;

    public PersonalAccountController() {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currentPath = req.getPathInfo();
        Long userId = Long.valueOf(currentPath.substring(1));
        User sessionUser = (User) req.getSession().getAttribute("user");
        User userFromDataBase = userService.getUserById(userId);

        // condition can be true only if user opens his own account
        if (Objects.nonNull(sessionUser) && sessionUser.equals(userFromDataBase)) {
            req.setAttribute("UserOwnAccount", true);
        }
        req.setAttribute("user", userFromDataBase);
        req.getRequestDispatcher(PATH_TO_PERSONAL_ACCOUNT_JSP).forward(req, resp);
    }
}
