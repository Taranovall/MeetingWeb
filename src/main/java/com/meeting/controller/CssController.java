package com.meeting.controller;

import com.meeting.util.Constant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebServlet(name = "css", urlPatterns = "/static/*")
public class CssController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cssFileName = req.getPathInfo();
        resp.setContentType("text/css");
        req.getRequestDispatcher(Constant.PATH_TO_STATIC_FOLDER + cssFileName).forward(req, resp);
    }
}
