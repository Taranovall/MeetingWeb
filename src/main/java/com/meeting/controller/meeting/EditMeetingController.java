package com.meeting.controller.meeting;

import com.meeting.entitiy.Meeting;
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

@WebServlet(name = "editMeeting", urlPatterns = "/meeting/edit-meeting")
public class EditMeetingController extends HttpServlet {

    private final MeetingService meetingService;
    private final ValidationService validationService;

    public EditMeetingController() {
        this.meetingService = new MeetingServiceImpl();
        this.validationService = new ValidationServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String lastURI = (String) session.getAttribute("lastPageURI");
        Meeting meeting = (Meeting) session.getAttribute("meeting");
        session.removeAttribute("meeting");

        String newMeetingTime = req.getParameter("meetingTime");
        String newMeetingDate = req.getParameter("meetingDate");
        String newMeetingPlace = req.getParameter("meetingPlace");

        meeting.setTime(newMeetingTime);
        meeting.setDate(newMeetingDate);
        meeting.setPlace(newMeetingPlace);

        if (validationService.meetingMainInfoValidator(meeting, req)) {
            meetingService.updateInformation(meeting);
        }
        resp.sendRedirect(lastURI);
    }
}
