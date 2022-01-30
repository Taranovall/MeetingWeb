package com.meeting.controllers;

import com.meeting.entitiy.Meeting;
import com.meeting.service.MeetingService;
import com.meeting.service.ValidationService;
import com.meeting.service.impl.MeetingServiceImpl;
import com.meeting.service.impl.ValidationServiceImpl;
import com.meeting.util.SearchQueryUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.meeting.util.Constant.IS_FORM_HAS_BEEN_USED_ATTRIBUTE_NAME;
import static com.meeting.util.Constant.MEETING_ATTRIBUTE_NAME;

@WebServlet(name = "searchMeeting", urlPatterns = "/search-meeting")
public class SearchMeetingController extends HttpServlet {

    private final MeetingService meetingService;
    private final ValidationService validationService;

    public SearchMeetingController() {
        this.meetingService = new MeetingServiceImpl();
        this.validationService = new ValidationServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // puts query in a session
        String query = req.getParameter("query");
        HttpSession session = req.getSession();

        List<Meeting> meetingList = meetingService.getAllMeetings();
        if (query != null) {
            boolean isValid = validationService.isQueryValid(query, req.getSession());
            if (isValid) {
                SearchQueryUtil.executeQuery(meetingList, query);
                session.setAttribute(MEETING_ATTRIBUTE_NAME, meetingList);
                session.removeAttribute("queryIsNotValid");
            }
        }

        session.setAttribute(IS_FORM_HAS_BEEN_USED_ATTRIBUTE_NAME, true);
        resp.sendRedirect("/");
    }
}
