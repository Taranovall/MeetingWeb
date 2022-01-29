package com.meeting.controllers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "searchMeeting", urlPatterns = "/search-meeting")
public class SearchMeetingController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // puts query in a session and makes redirect to main page where it is handled
        String query = req.getParameter("query");
        req.getSession().setAttribute("query", query);
        resp.sendRedirect("/");
    }
}
