package com.meeting.controller;

import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.service.UserService;
import com.meeting.service.ValidationService;
import com.meeting.service.impl.UserServiceImpl;
import com.meeting.service.impl.ValidationServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "setEmail", urlPatterns = "/account/set-email")
public class SetEmailController extends HttpServlet {

    private final UserService userService;
    private final ValidationService validationService;

    public SetEmailController() {
        this.userService = new UserServiceImpl();
        this.validationService = new ValidationServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        // if user has any error attribute is moved from session to request
        if (session.getAttribute("error") != null) {
            req.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }
        String redirectTo = (String) session.getAttribute("lastPageURI");
        User user = (User) session.getAttribute("user");
        String email = req.getParameter("email");

        if (validationService.emailValidator(email, req)) {
            try {
                userService.setEmail(user.getId(), email);
                user.setEmail(email);
                session.setAttribute("user", user
                );
                resp.sendRedirect(redirectTo);
            } catch (DataBaseException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }
}
