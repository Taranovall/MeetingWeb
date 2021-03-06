package com.meeting.controller;

import com.meeting.entitiy.User;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.ValidationService;
import com.meeting.service.impl.ValidationServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger log = LogManager.getLogger(LoginController.class);

    private ValidationService validationService;

    public LoginController() {
        this.validationService = new ValidationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(PATH_TO_LOGIN_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = null;
        try {
            user = validationService.authValidator(req);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        String redirectTo = (String) req.getSession().getAttribute("lastPageURI");

        if (nonNull(req.getAttribute("message"))) {
            doGet(req, resp);
        } else {
            req.getSession().setAttribute("user", user);
            if (redirectTo != null) {
                resp.sendRedirect(redirectTo);
            } else {
                resp.sendRedirect("/");
            }
            log.info("{}[{}] just signed in", user.getLogin(), user.getId());
        }
    }

    public void setValidationService(ValidationService validationService) {
        this.validationService = validationService;
    }
}
