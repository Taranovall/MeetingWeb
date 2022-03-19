package com.meeting.controller;

import com.meeting.entitiy.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "logout", urlPatterns = "/logout")
public class LogoutController extends HttpServlet {

    private static final Logger log = LogManager.getLogger(LogoutController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String redirectTo = (String) session.getAttribute("lastPageURI");
        User user = (User) session.getAttribute("user");
        req.getSession().invalidate();
        log.info("{}[{}] just logged out", user.getLogin(), user.getId());
        if (redirectTo != null) {
            resp.sendRedirect(redirectTo);
        } else {
            resp.sendRedirect("/");
        }
    }
}
