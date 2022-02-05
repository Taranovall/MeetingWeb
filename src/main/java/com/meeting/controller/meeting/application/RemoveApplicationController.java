package com.meeting.controller.meeting.application;

import com.meeting.entitiy.User;
import com.meeting.service.SpeakerService;
import com.meeting.service.impl.SpeakerServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "removeApplication", urlPatterns = "/meeting/remove-application")
public class RemoveApplicationController extends HttpServlet {

    private final SpeakerService speakerService;

    public RemoveApplicationController() {
        this.speakerService = new SpeakerServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lastURI = (String) req.getSession().getAttribute("lastPageURI");
        String topicId = req.getParameter("application");
        User userSession = (User) req.getSession().getAttribute("user");

        if (topicId == null) resp.sendRedirect(lastURI);

        speakerService.removeApplication(Long.parseLong(topicId), userSession.getId());

        List<String> applicationList = (List<String>) req.getSession().getAttribute("applicationList");
        applicationList.remove(topicId);
        req.getSession().setAttribute("applicationList", applicationList);
        resp.sendRedirect(lastURI);
    }
}
