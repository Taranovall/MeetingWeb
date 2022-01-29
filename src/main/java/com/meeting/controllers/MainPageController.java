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
        Meeting meeting = new Meeting(1L,"InvestPro Online 2022 Dubai Preview","26.01.2022","17:00 - 21:00", "Offline");
        Meeting meeting1 = new Meeting(2L,"InvestPro Offline 2022 Dubai Preview","26.01.2022","17:00 - 21:00", "Dubai");
        List<Meeting> meetingList = meetingService.getAllMeetings();
        req.setAttribute("meetings", meetingList);
        req.getRequestDispatcher(PATH_TO_MAIN_PAGE_JSP).forward(req, resp);
    }
}
