package com.meeting.controller.meeting;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Speaker;
import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.service.MeetingService;
import com.meeting.service.SpeakerService;
import com.meeting.service.ValidationService;
import com.meeting.service.impl.MeetingServiceImpl;
import com.meeting.service.impl.SpeakerServiceImpl;
import com.meeting.service.impl.ValidationServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.meeting.util.Constant.*;

@WebServlet(name = "createMeeting", urlPatterns = "/moderator/create-meeting")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class CreateMeetingController extends HttpServlet {

    public static final String MEETING = "meeting";
    private MeetingService meetingService;
    private SpeakerService speakerService;
    private ValidationService validationService;

    public CreateMeetingController() {
        this.meetingService = new MeetingServiceImpl();
        this.speakerService = new SpeakerServiceImpl();
        this.validationService = new ValidationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute(LAST_PAGE_URI, req.getRequestURI());
        if (req.getParameterMap().size() == 0) {
            req.getRequestDispatcher(PATH_TO_CREATE_MEETING_FIRST_PAGE_JSP).forward(req, resp);
        } else {
            // retrieve get parameters
            String name = req.getParameter("name");
            String date = req.getParameter("date");
            String startTime = req.getParameter("startTime");
            String endTime = req.getParameter("endTime");
            String place = req.getParameter("place");
            // create meeting from get parameters
            Meeting meeting = new Meeting(name, date, startTime, endTime, place);
            // check if parameters are valid
            if (validationService.meetingMainInfoValidator(meeting, req)) {
                List<Speaker> speakerList = null;
                try {
                    speakerList = speakerService.getAllSpeakers();
                } catch (DataBaseException e) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
                session.setAttribute(SPEAKERS, speakerList);
                session.setAttribute(MEETING, meeting);
                session.setAttribute(COUNT_OF_TOPICS, req.getParameter(COUNT_OF_TOPICS));
                session.setAttribute(FIRST_PAGE_URL, req.getRequestURI());
                session.removeAttribute(ERROR);
                req.getRequestDispatcher(PATH_TO_CREATE_MEETING_SECOND_PAGE_JSP).forward(req, resp);
            } else {
                req.getRequestDispatcher(PATH_TO_CREATE_MEETING_FIRST_PAGE_JSP).forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // removes error attribute in case if user got any error before successful input
        HttpSession session = req.getSession();
        session.setAttribute(LAST_PAGE_URI, req.getRequestURI());
        session.removeAttribute(ERROR);
        String[] topics = req.getParameterValues("topicName");
        String[] speakers = req.getParameterValues("speakerName");

        Meeting meeting = (Meeting) req.getSession().getAttribute(MEETING);

        Part uploadedImage = req.getPart("photo");

        if (validationService.meetingPostValidator(topics, uploadedImage, req)) {
            // creates temp file and write image in it
            File image = new File(uploadedImage.getSubmittedFileName());
            uploadedImage.write(image.getAbsolutePath());
            User userFromSession = (User) req.getSession().getAttribute("user");
            try {
                meetingService.createMeeting(userFromSession, meeting, topics, speakers, image);
                // removes because it already doesn't need
                session.removeAttribute(COUNT_OF_TOPICS);
                session.removeAttribute(FIRST_PAGE_URL);
                session.removeAttribute(SPEAKERS);
                resp.sendRedirect("/");
            } catch (DataBaseException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            req.getRequestDispatcher(PATH_TO_CREATE_MEETING_SECOND_PAGE_JSP).forward(req, resp);
        }
    }

    public void setMeetingService(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    public void setSpeakerService(SpeakerService speakerService) {
        this.speakerService = speakerService;
    }

    public void setValidationService(ValidationService validationService) {
        this.validationService = validationService;
    }
}


