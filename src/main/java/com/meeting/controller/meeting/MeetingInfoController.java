package com.meeting.controller.meeting;

import com.meeting.entitiy.*;
import com.meeting.exception.DataBaseException;
import com.meeting.service.MeetingService;
import com.meeting.service.SpeakerService;
import com.meeting.service.impl.MeetingServiceImpl;
import com.meeting.service.impl.SpeakerServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

import static com.meeting.util.Constant.PATH_TO_MEETING_INFO_PAGE;

@WebServlet(name = "meetingInformation", urlPatterns = "/meeting/*")
public class MeetingInfoController extends HttpServlet {

    private final MeetingService meetingService;
    private final SpeakerService speakerService;

    public MeetingInfoController() {
        this.meetingService = new MeetingServiceImpl();
        this.speakerService = new SpeakerServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currentPath = req.getPathInfo();
        Long meetingId = Long.valueOf(currentPath.substring(1));

        HttpSession session = req.getSession();
        User userSession = (User) req.getSession().getAttribute("user");

        if (session.getAttribute("error") != null) {
            req.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }

        Meeting meeting = null;
        try {
            meeting = meetingService.getMeetingById(meetingId);

            req.setAttribute("meeting", meeting);
            req.getSession().setAttribute("lastPageURI", req.getRequestURI());
                if (Objects.nonNull(userSession)) {
                    if (userSession.getRole() == Role.SPEAKER){
                        List<String> sentApplicationList = (List<String>) session.getAttribute("sentApplicationList");
                        if (sentApplicationList == null) {
                            sentApplicationList = speakerService.getSentApplications(userSession.getId());
                            session.setAttribute("sentApplicationList", sentApplicationList);
                        }

                        List<String> receivedApplicationList = speakerService.getReceivedApplications(userSession.getId());
                        if (receivedApplicationList != null) {
                            session.setAttribute("receivedApplicationList", receivedApplicationList);
                        }
                    } else if (userSession.getRole() == Role.MODERATOR) {
                        session.setAttribute("sentApplicationsBySpeaker", meeting.getSentApplicationsMap());
                        session.setAttribute("proposedTopics", meeting.getProposedTopicsMap());
                    }
                }

            req.getRequestDispatcher(PATH_TO_MEETING_INFO_PAGE).forward(req, resp);
        } catch (DataBaseException e) {
            e.printStackTrace();
        }
    }
}