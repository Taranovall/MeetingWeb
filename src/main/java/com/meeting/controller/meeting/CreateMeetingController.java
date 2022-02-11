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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.meeting.util.Constant.*;

@WebServlet(name = "createMeeting", urlPatterns = "/create-meeting")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class CreateMeetingController extends HttpServlet {

    private final MeetingService meetingService;
    private final SpeakerService speakerService;
    private final ValidationService validationService;

    public CreateMeetingController() {
        this.meetingService = new MeetingServiceImpl();
        this.speakerService = new SpeakerServiceImpl();
        this.validationService = new ValidationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameterMap().size() == 0) {
            req.getRequestDispatcher(PATH_TO_CREATE_MEETING_FIRST_PAGE_JSP).forward(req, resp);
        } else {
            // retrieve get parameters
            String name = req.getParameter("name");
            String date = req.getParameter("date");
            String time = req.getParameter("time");
            String place = req.getParameter("place");
            // create meeting from get parameters
            Meeting meeting = new Meeting(name, date, time, place);
            // check if parameters are valid
            if (validationService.createMeetingGetValidator(meeting, req)) {
                List<Speaker> speakerList = speakerService.getAllSpeakers();
                req.setAttribute("speakers", speakerList);
                req.getSession().setAttribute("meeting", meeting);
                req.getSession().setAttribute("countOfTopics", req.getParameter("countOfTopics"));
                req.getSession().setAttribute("firstPageURL", req.getRequestURI());
                req.getRequestDispatcher(PATH_TO_CREATE_MEETING_SECOND_PAGE_JSP).forward(req, resp);
            } else {
                req.getRequestDispatcher(PATH_TO_CREATE_MEETING_FIRST_PAGE_JSP).forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // removes error attribute in case if user got any error before successful input
        req.getSession().removeAttribute("error");
        String[] topics = req.getParameterValues("topicName");
        String[] speakers = req.getParameterValues("speakerName");

        Meeting meeting = (Meeting) req.getSession().getAttribute("meeting");

        Part uploadedImage = req.getPart("photo");

        if (validationService.createMeetingPostValidator(topics, uploadedImage, req)) {

            // creates temp file and write image in it
            File image = new File(uploadedImage.getSubmittedFileName());
            uploadedImage.write(image.getAbsolutePath());

            User userFromSession = (User) req.getSession().getAttribute("user");
            try {
                meetingService.createMeeting(userFromSession, meeting, topics, speakers, image);
                // removes because it already doesn't need
                req.getSession().removeAttribute("countOfTopics");
                req.getSession().removeAttribute("firstPageURL");
                resp.reset();
            } catch (DataBaseException e) {
                e.printStackTrace();
            }
        } else {
            req.getRequestDispatcher(PATH_TO_CREATE_MEETING_SECOND_PAGE_JSP).forward(req, resp);
        }
    }
}


