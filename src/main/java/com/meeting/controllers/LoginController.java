package com.meeting.controllers;

import com.meeting.entitiy.User;
import com.meeting.service.ValidationService;
import com.meeting.service.impl.ValidationServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.meeting.util.Constant.PATH_TO_LOGIN_JSP;
import static java.util.Objects.nonNull;

@WebServlet(name = "login", urlPatterns = "/login")
public class LoginController extends HttpServlet {

    private final ValidationService validationService;

    public LoginController() {
        this.validationService = new ValidationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(PATH_TO_LOGIN_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession session = req.getSession();

        User user = validationService.authValidator(req);
        session.setAttribute("user", user);

        if (nonNull(req.getAttribute("message"))) {
            doGet(req, resp);
        } else {
            System.out.printf("User with id %d just signed in", user.getId());
        }
    }
}
