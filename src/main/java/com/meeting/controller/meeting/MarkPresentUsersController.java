package com.meeting.controller.meeting;

import com.meeting.exception.DataBaseException;
import com.meeting.service.MeetingService;
import com.meeting.service.impl.MeetingServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "markUsers", urlPatterns = "/moderator/meeting/mark-present-users/*")
public class MarkPresentUsersController extends HttpServlet {

    private final MeetingService meetingService;

    public MarkPresentUsersController() {
        this.meetingService = new MeetingServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lastURI = (String) req.getSession().getAttribute("lastPageURI");
        String[] presentUsers = req.getParameterValues("presentUserId");
        Long meetingId = Long.valueOf(req.getPathInfo().split("/")[1]);
        req.getParameterMap();
        try {
            meetingService.markPresentUsers(presentUsers, meetingId);
        }  catch (DataBaseException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        resp.sendRedirect(lastURI);
    }
}
