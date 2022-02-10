package com.meeting.controller.meeting.application;

import com.meeting.entitiy.Role;
import com.meeting.entitiy.User;
import com.meeting.service.SpeakerService;
import com.meeting.service.impl.SpeakerServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(name = "acceptApplication", urlPatterns = "/meeting/accept-application")
public class AcceptApplicationController extends HttpServlet {
    private final SpeakerService speakerService;

    public AcceptApplicationController() {
        this.speakerService = new SpeakerServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String speakerId = req.getParameter("speakerId");
        String lastURI = (String) req.getSession().getAttribute("lastPageURI");

        if (speakerId.equals("none")) {
            req.getSession().setAttribute("error", "Option hasn't been selected");
        } else {

            String topicId = req.getParameter("application");
            User userSession = (User) req.getSession().getAttribute("user");

            if (topicId == null || !userSession.getRole().equals(Role.MODERATOR)) resp.sendRedirect(lastURI);

            speakerService.acceptApplication(Long.parseLong(topicId), Long.parseLong(speakerId));
        }

        resp.sendRedirect(lastURI);
    }
}