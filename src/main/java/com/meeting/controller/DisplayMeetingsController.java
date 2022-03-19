package com.meeting.controller;

import com.meeting.entitiy.Meeting;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.MeetingService;
import com.meeting.service.impl.MeetingServiceImpl;
import com.meeting.util.DisplayedMeetingUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.meeting.util.Constant.IS_FORM_HAS_BEEN_USED_ATTRIBUTE_NAME;
import static com.meeting.util.Constant.MEETING_ATTRIBUTE_NAME;

@WebServlet(name = "showMeetings", urlPatterns = "/show-meetings")
public class DisplayMeetingsController extends HttpServlet {

    private MeetingService meetingService;

    public DisplayMeetingsController() {
        this.meetingService = new MeetingServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String displayOption = req.getParameter("radioButton");
        if (Objects.nonNull(displayOption)) {
        List<Meeting> meetingList = null;
        try {
            meetingList = meetingService.getAllMeetings();
            meetingList = DisplayedMeetingUtil.displayMeetings(meetingList, displayOption);
        }  catch (DataBaseException | UserNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        session.setAttribute(MEETING_ATTRIBUTE_NAME, meetingList);
        session.setAttribute(IS_FORM_HAS_BEEN_USED_ATTRIBUTE_NAME, displayOption);
        } else {
            session.setAttribute("error", "You didn't choose any option");
        }
        resp.sendRedirect("/");
    }

    public void setMeetingService(MeetingService meetingService) {
        this.meetingService = meetingService;
    }
}
