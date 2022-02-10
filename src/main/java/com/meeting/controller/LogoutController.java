package com.meeting.controller;

import com.meeting.entitiy.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "logout", urlPatterns = "/logout")
public class LogoutController extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(LogoutController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
