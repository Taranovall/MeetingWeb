package com.meeting.controller;

import com.meeting.entitiy.Meeting;
import com.meeting.entitiy.Role;
import com.meeting.entitiy.Speaker;
import com.meeting.entitiy.User;
import com.meeting.service.MeetingService;
import com.meeting.service.SpeakerService;
import com.meeting.service.UserService;
import com.meeting.service.impl.MeetingServiceImpl;
import com.meeting.service.impl.SpeakerServiceImpl;
import com.meeting.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import static com.meeting.util.Constant.PATH_TO_PERSONAL_ACCOUNT_JSP;

@WebServlet(name = "personalAccount", urlPatterns = "/account/*")
public class PersonalAccountController extends HttpServlet {

    private final UserService userService;
    private final SpeakerService speakerService;
    private final MeetingService meetingService;

    public PersonalAccountController() {
        this.userService = new UserServiceImpl();
        this.speakerService = new SpeakerServiceImpl();
        this.meetingService = new MeetingServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String currentPath = req.getPathInfo();
        Long userId = Long.valueOf(currentPath.substring(1));
        User sessionUser = (User) req.getSession().getAttribute("user");
        User userFromDataBase = userService.getUserById(userId);

        Speaker speaker = null;

        if (userFromDataBase.getRoles().contains(Role.SPEAKER)) {
             speaker = speakerService.getSpeakerById(userFromDataBase.getId());
            Set<Meeting> speakerMeetings = meetingService.getMeetingsSpeakerIsInvolvedIn(speaker.getId());
            req.setAttribute("meetingsSpeakerIsInvolvedIn", speakerMeetings);
        }
        // condition can be true only if user opens his own account
        if (Objects.nonNull(sessionUser) && sessionUser.equals(userFromDataBase)) {
            req.setAttribute("UserOwnAccount", true);
        }
        req.setAttribute("user", userFromDataBase);
        req.getSession().setAttribute("lastPageURI", req.getRequestURI());
        req.getRequestDispatcher(PATH_TO_PERSONAL_ACCOUNT_JSP).forward(req, resp);
    }
}
