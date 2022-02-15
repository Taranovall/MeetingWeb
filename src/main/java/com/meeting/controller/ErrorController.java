package com.meeting.controller;

import com.meeting.util.Constant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "errorController", urlPatterns = "/error")
public class ErrorController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("type", "Exception Report");
        req.setAttribute("message", req.getAttribute("javax.servlet.error.message"));
        req.getRequestDispatcher(Constant.PATH_TO_ERROR_JSP).forward(req, resp);
    }
}
