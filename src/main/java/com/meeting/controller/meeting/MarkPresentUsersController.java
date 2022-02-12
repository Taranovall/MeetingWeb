package com.meeting.controller.meeting;

import com.meeting.entitiy.Meeting;
import com.meeting.exception.DataBaseException;
import com.meeting.service.MeetingService;
import com.meeting.service.impl.MeetingServiceImpl;
import com.meeting.util.Constant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "markUsers", urlPatterns = "/meeting/moderator/mark-present-users/*")
public class MarkPresentUsersController extends HttpServlet {

    private final MeetingService meetingService;

    public MarkPresentUsersController() {
        this.meetingService = new MeetingServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lastURI = (String) req.getSession().getAttribute("lastPageURI");
        Long meetingId = Long.valueOf(req.getPathInfo().split("/")[1]);
        Meeting meeting = null;
        try {
            meeting = meetingService.getMeetingById(meetingId);
        } catch (DataBaseException e) {
            e.printStackTrace();
        }
        req.setAttribute("meeting", meeting);
        req.getRequestDispatcher(Constant.PATH_TO_MARK_PRESENT_USERS_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] presentUsers = req.getParameterValues("presentUserId");
        req.getParameterMap();
    }
}
