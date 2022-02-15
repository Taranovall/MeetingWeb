package com.meeting.controller.meeting.application;

import com.meeting.entitiy.User;
import com.meeting.exception.DataBaseException;
import com.meeting.service.SpeakerService;
import com.meeting.service.impl.SpeakerServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "acceptInvitation", urlPatterns = "/speaker/meeting/accept-invitation")
public class AcceptInvitationController extends HttpServlet {

    private final SpeakerService speakerService;

    public AcceptInvitationController() {
        this.speakerService = new SpeakerServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String lastURI = (String) req.getSession().getAttribute("lastPageURI");
        String topicId = req.getParameter("application");
        User userSession = (User) req.getSession().getAttribute("user");

        if (topicId == null) resp.sendRedirect(lastURI);

        try {
            speakerService.acceptInvitation(Long.parseLong(topicId), userSession.getId());
        } catch (DataBaseException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        resp.sendRedirect(lastURI);
    }
}
