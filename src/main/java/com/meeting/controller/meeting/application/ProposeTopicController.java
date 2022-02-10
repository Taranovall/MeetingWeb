package com.meeting.controller.meeting.application;

import com.meeting.entitiy.User;
import com.meeting.service.MeetingService;
import com.meeting.service.impl.MeetingServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "proposeTopic", urlPatterns = "/meeting/propose-topic")
public class ProposeTopicController  extends HttpServlet {

    private final MeetingService meetingService;

    public ProposeTopicController() {
        this.meetingService = new MeetingServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String lastURI = (String) session.getAttribute("lastPageURI");
        String topicName = req.getParameter("topicName");
        User user = (User) session.getAttribute("user");
        Long meetingId = Long.valueOf(lastURI.split("/")[2]);

        meetingService.proposeTopic(meetingId, user.getId(), topicName);

        resp.sendRedirect(lastURI);
    }
}