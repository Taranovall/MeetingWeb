package com.meeting.controller.meeting;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Speaker;
import com.meeting.entitiy.User;
import com.meeting.service.MeetingService;
import com.meeting.service.SpeakerService;
import com.meeting.service.impl.MeetingServiceImpl;
import com.meeting.service.impl.SpeakerServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.meeting.util.Constant.PATH_TO_CREATE_MEETING_FIRST_PAGE_JSP;
import static com.meeting.util.Constant.PATH_TO_CREATE_MEETING_SECOND_PAGE_JSP;

@WebServlet(name = "createMeeting", urlPatterns = "/create-meeting")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class CreateMeetingController extends HttpServlet {

    private final MeetingService meetingService;
    private final SpeakerService speakerService;

    public CreateMeetingController() {
        this.meetingService = new MeetingServiceImpl();
        this.speakerService = new SpeakerServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getParameterMap().size() == 0) {
            req.getRequestDispatcher(PATH_TO_CREATE_MEETING_FIRST_PAGE_JSP).forward(req, resp);
        } else {
            req.getSession().setAttribute("parameters", arrayValueToString(req.getParameterMap()));
            List<Speaker> speakerList = speakerService.getAllSpeakers();
            req.setAttribute("speakers", speakerList);
            req.setAttribute("countOfTopics", req.getParameter("countOfTopics"));
            req.getRequestDispatcher(PATH_TO_CREATE_MEETING_SECOND_PAGE_JSP).forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] topics = req.getParameterValues("topicName");
        String[] speakers = req.getParameterValues("speakerName");

        Meeting meeting = new Meeting();


        Map<String, String> parameters = (Map<String, String>) req.getSession().getAttribute("parameters");

        meeting.setName(parameters.get("name"));
        meeting.setDate(parameters.get("date"));
        meeting.setTime(parameters.get("time"));
        meeting.setPlace(parameters.get("place"));

        Part uploadedImage = req.getPart("photo");

        // creates temp file and write image in it
        File image = new File(uploadedImage.getSubmittedFileName());
        uploadedImage.write(image.getAbsolutePath());

        User userFromSession = (User) req.getSession().getAttribute("user");

        meetingService.createMeeting(userFromSession, meeting, topics, speakers, image);


    }

    private Map<String, String> arrayValueToString(Map<String, String[]> map) {
        Map<String, String> result = new LinkedHashMap<>();
        map.forEach((k, v) -> result.put(k, String.join(" ", v)));
        return result;
    }

}


