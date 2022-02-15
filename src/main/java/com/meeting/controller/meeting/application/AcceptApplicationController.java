package com.meeting.controller.meeting.application;

import com.meeting.entitiy.Role;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.service.SpeakerService;
import com.meeting.service.ValidationService;
import com.meeting.service.impl.SpeakerServiceImpl;
import com.meeting.service.impl.ValidationServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "acceptApplication", urlPatterns = "/moderator/meeting/accept-application")
public class AcceptApplicationController extends HttpServlet {
    private final SpeakerService speakerService;
    private final ValidationService validationService;

    public AcceptApplicationController() {
        this.speakerService = new SpeakerServiceImpl();
        this.validationService = new ValidationServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String speakerId = req.getParameter("speakerId");
        String lastURI = (String) req.getSession().getAttribute("lastPageURI");

        if (validationService.chooseSpeakerValidator(speakerId, req)) {
            String topicId = req.getParameter("application");
            User userSession = (User) req.getSession().getAttribute("user");

            if (topicId == null || !userSession.getRole().equals(Role.MODERATOR)) resp.sendRedirect(lastURI);
            try {
                speakerService.acceptApplication(Long.parseLong(topicId), Long.parseLong(speakerId));
            } catch (DataBaseException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }

        resp.sendRedirect(lastURI);

    }
}
