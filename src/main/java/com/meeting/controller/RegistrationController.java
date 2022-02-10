package com.meeting.controller;

import com.meeting.entitiy.User;
import com.meeting.service.UserService;
import com.meeting.service.ValidationService;
import com.meeting.exception.DataBaseException;
import com.meeting.service.impl.UserServiceImpl;
import com.meeting.service.impl.ValidationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.meeting.util.Constant.PATH_TO_REGISTRATION_JSP;

@WebServlet(name = "registration", urlPatterns = "/registration")
public class RegistrationController extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);
    private final UserService userService;
    private final ValidationService validationService;

    public RegistrationController() {
        this.userService = new UserServiceImpl();
        this.validationService = new ValidationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(PATH_TO_REGISTRATION_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = validationService.registrationValidator(req);

        if (user == null) {
            doGet(req, resp);
        } else {
            try {
                userService.signUpUser(user);
                log.info("{}[{}] just signed up", user.getLogin(), user.getId());
            } catch (DataBaseException e) {
                e.printStackTrace();
                log.error("Sign up error: {}", e.getMessage());
            }
        }
    }
}
