package com.meeting.controller.meeting.application;

import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.service.MeetingService;
import com.meeting.service.ValidationService;
import com.meeting.service.impl.MeetingServiceImpl;
import com.meeting.service.impl.ValidationServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "proposeTopic", urlPatterns = "/speaker/meeting/propose-topic")
public class ProposeTopicController extends HttpServlet {

    private final MeetingService meetingService;
    private final ValidationService validationService;

    public ProposeTopicController() {
        this.meetingService = new MeetingServiceImpl();
        this.validationService = new ValidationServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String lastURI = (String) session.getAttribute("lastPageURI");
        String topicName = req.getParameter("topicName");
        User user = (User) session.getAttribute("user");
        Long meetingId = Long.valueOf(lastURI.split("/")[2]);

        // topic will be proposed only if topic name is valid
        if (validationService.proposingTopicsValidator(topicName, req)) {
            try {
                meetingService.proposeTopic(meetingId, user.getId(), topicName);
            } catch (DataBaseException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }

        resp.sendRedirect(lastURI);
    }
}
