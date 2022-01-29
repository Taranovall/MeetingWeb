package com.meeting.controllers;

import com.meeting.entitiy.User;
import com.meeting.service.ValidationService;
import com.meeting.service.impl.ValidationServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        System.out.println("Session ID: " + req.getSession().getId());
        User sessionUser = (User) req.getSession().getAttribute("user");
        if (sessionUser != null) {
            resp.getWriter().write("You're already logged");
        } else {
            req.getRequestDispatcher(PATH_TO_LOGIN_JSP).forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = validationService.authValidator(req);
        req.getSession().setAttribute("user", user);

        if (nonNull(req.getAttribute("message"))) {
            doGet(req, resp);
        } else {
            resp.sendRedirect("/");
            System.out.printf("User with id %d just signed in", user.getId());
        }
    }
}
