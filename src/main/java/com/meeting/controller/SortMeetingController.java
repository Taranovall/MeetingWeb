package com.meeting.controller;

import com.meeting.entitiy.Meeting;
import com.meeting.service.impl.MeetingServiceImpl;
import com.meeting.util.SortingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import static com.meeting.util.Constant.SORT_METHOD_ATTRIBUTE_NAME;

@WebServlet(name = "sortMeeting", urlPatterns = "/sort-meeting")
public class SortMeetingController extends HttpServlet {
    private static final Logger log = LogManager.getLogger(MeetingServiceImpl.class);
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String sortMethod = req.getParameter(SORT_METHOD_ATTRIBUTE_NAME);
        List<Meeting> meetingList = (List<Meeting>) session.getAttribute(MEETING_ATTRIBUTE_NAME);

        meetingList = SortingUtil.sortByParameter(meetingList, sortMethod);

        session.setAttribute(MEETING_ATTRIBUTE_NAME, meetingList);
        session.setAttribute(IS_FORM_HAS_BEEN_USED_ATTRIBUTE_NAME, sortMethod);
        log.debug("User {} just sorted meetings", session.getId());
        resp.sendRedirect("/");
    }
}
