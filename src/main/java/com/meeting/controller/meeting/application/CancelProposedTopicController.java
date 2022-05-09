package com.meeting.controller.meeting.application;

import com.meeting.exception.DataBaseException;
import com.meeting.service.MeetingService;
import com.meeting.service.impl.MeetingServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "cancelProposedTopic", urlPatterns = "/moderator/meeting/cancel-proposition")
public class CancelProposedTopicController extends HttpServlet {

    private MeetingService meetingService;

    public CancelProposedTopicController() {
        this.meetingService = new MeetingServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String lastURI = (String) session.getAttribute("lastPageURI");
        Long topicId = Long.valueOf(req.getParameter("application"));

        try {
            meetingService.cancelProposedTopic(topicId);
        } catch (DataBaseException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        resp.sendRedirect(lastURI);
    }

    public void setMeetingService(MeetingService meetingService) {
        this.meetingService = meetingService;
    }
}
