package com.meeting.controller.meeting;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.service.UserService;
import com.meeting.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "participate", urlPatterns = "/meeting/participate")
public class ParticipateController extends HttpServlet {

    private final UserService userService;

    public ParticipateController() {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lastURI = (String) req.getSession().getAttribute("lastPageURI");
        User userSession = (User) req.getSession().getAttribute("user");
        Meeting meeting = (Meeting) req.getSession().getAttribute("meeting");
        Long meetingId = meeting.getId();

        try {
            userService.participate(userSession.getId(), meetingId);
            userSession.getMeetingIdsSetUserTakesPart().add(meetingId);
        }  catch (DataBaseException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        resp.sendRedirect(lastURI);
    }
}
