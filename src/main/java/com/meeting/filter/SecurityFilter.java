package com.meeting.filter;

import com.meeting.entitiy.Role;
import com.meeting.entitiy.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebFilter(filterName = "securityFilet", urlPatterns = "/create-meeting")
public class SecurityFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (req.getRequestURI().equals("/create-meeting")) {
            User user = (User) req.getSession().getAttribute("user");
            if (user != null && user.getRole().equals(Role.MODERATOR)) {
                chain.doFilter(req, resp);
            }
        }
        resp.sendRedirect("/");
    }

    @Override
    public void destroy() {

    }
}
