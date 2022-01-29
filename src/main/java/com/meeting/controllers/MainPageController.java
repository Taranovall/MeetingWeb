package com.meeting.controllers;

import com.meeting.entitiy.Meeting;
import com.meeting.service.MeetingService;
import com.meeting.service.impl.MeetingServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
        HttpSession session = req.getSession();
        String attrName = "meetings";
        List<Meeting> meetingList = null;
        // it can be true only if user has made search query
        boolean isUserMadeSearchQuery = session.getAttribute("query") != null;
        if (isUserMadeSearchQuery) {
             meetingList = (List<Meeting>) session.getAttribute(attrName);
        }

        if (meetingList == null) {
            meetingList = meetingService.getAllMeetings();
            req.setAttribute(attrName, meetingList);

            //removes those attributes in case if user has made some search queries before reloading the page
            if (!isUserMadeSearchQuery) {
                session.removeAttribute("meetings");
                session.removeAttribute("queryIsNotValid");
            }
        }
        session.removeAttribute("query");
        req.getRequestDispatcher(PATH_TO_MAIN_PAGE_JSP).forward(req, resp);
    }
}
