package com.meeting.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "logout", urlPatterns = "/logout")
public class LogoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String redirectTo = (String) req.getSession().getAttribute("lastPageURI");
        req.getSession().invalidate();
        if (redirectTo != null) {
            resp.sendRedirect(redirectTo);
        } else {
            resp.sendRedirect("/");
        }
    }
}
