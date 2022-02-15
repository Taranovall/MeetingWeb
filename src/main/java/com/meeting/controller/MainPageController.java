package com.meeting.controller;

import com.meeting.entitiy.Meeting;
import com.meeting.exception.DataBaseException;
import com.meeting.exception.UserNotFoundException;
import com.meeting.service.MeetingService;
import com.meeting.service.impl.MeetingServiceImpl;
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
import java.util.stream.Collectors;

import static com.meeting.util.Constant.*;

@WebServlet(name = "meetings", urlPatterns = "")
public class MainPageController extends HttpServlet {

    private static final Logger log = LogManager.getLogger(MainPageController.class);
    private final MeetingService meetingService;

    public MainPageController() {
        this.meetingService = new MeetingServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        List<Meeting> meetingList = null;
        // it can be true only if user has made search query or choose any sort method
        boolean isUserHasUsedForm = session.getAttribute(IS_FORM_HAS_BEEN_USED_ATTRIBUTE_NAME) != null;
        if (isUserHasUsedForm || req.getParameter("page") != null) {
            meetingList = (List<Meeting>) session.getAttribute(MEETING_ATTRIBUTE_NAME);
            htmlReverseOrder(session);
        }
        // can be null if user open page for the first time or without parameters
        if (meetingList == null) {
            try {
                    meetingList = meetingService.getAllMeetings();
                } catch (DataBaseException | UserNotFoundException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }

            if (!isUserHasUsedForm) {
                session.removeAttribute("option");
                session.removeAttribute(SORT_METHOD_ATTRIBUTE_NAME);
                session.removeAttribute(QUERY_IS_NOT_VALID_ATTRIBUTE_NAME);
                session.removeAttribute("query");
            }
        }
        //pagination
        int maxPageCount = (meetingList.size() / 8) + 1;
        int pageNumber = 1;
        if (req.getParameter("page") != null) {
            pageNumber = Integer.parseInt(req.getParameter("page"));
        }
        List<Meeting> displayMeetings = meetingList.stream().skip((pageNumber - 1) * 8L).limit(8).collect(Collectors.toList());
        req.setAttribute("pageNumber", pageNumber);
        req.setAttribute("maxPageCount", maxPageCount);

        req.setAttribute(MEETING_ATTRIBUTE_NAME, displayMeetings);
        session.removeAttribute(IS_FORM_HAS_BEEN_USED_ATTRIBUTE_NAME);
        session.setAttribute("lastPageURI", req.getRequestURI());
        req.getRequestDispatcher(PATH_TO_MAIN_PAGE_JSP).forward(req, resp);
    }

    /**
     * Reverses order of meetings if sort method has been chosen for the second time in a row
     */
    private void htmlReverseOrder(HttpSession session) {
        Object previousSortingMethod = session.getAttribute(SORT_METHOD_ATTRIBUTE_NAME);
        Object currentSortingMethod = session.getAttribute(IS_FORM_HAS_BEEN_USED_ATTRIBUTE_NAME);
        /*
        if previous and current sort method are equals
        it means that user has chosen similar method of sorting one more time
        and order has to be reversed
         */
        if (previousSortingMethod instanceof String && previousSortingMethod.equals(currentSortingMethod)) {
            // removes text in brackets
            session.removeAttribute(SORT_METHOD_ATTRIBUTE_NAME);
        } else {
            session.setAttribute(SORT_METHOD_ATTRIBUTE_NAME, currentSortingMethod);
        }

    }
}
