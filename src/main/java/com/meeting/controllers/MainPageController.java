package com.meeting.controllers;

import com.meeting.entitiy.Meeting;
import com.meeting.service.MeetingService;
import com.meeting.service.impl.MeetingServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.meeting.util.Constant.PATH_TO_MAIN_PAGE_JSP;

@WebServlet(name = "meetings", urlPatterns = "")
public class MainPageController extends HttpServlet {

    private final MeetingService meetingService;

    public MainPageController() {
        this.meetingService = new MeetingServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = (String) req.getSession().getAttribute("query"); // returns null if search query wasn't made
        List<Meeting> meetingList = meetingService.getAllMeetings();
        // if search query was made then removes all meetings from list that don't contain search query ignoring case
        if (query != null) {
            meetingList.removeIf(meeting -> !meeting.getName().toLowerCase().contains(query.toLowerCase()));
            req.getSession().removeAttribute("query");
        }
        req.setAttribute("meetings", meetingList);
        req.getRequestDispatcher(PATH_TO_MAIN_PAGE_JSP).forward(req, resp);
    }
}
