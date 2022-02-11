package com.meeting.filter;

import com.meeting.service.UserService;
import com.meeting.service.impl.UserServiceImpl;

import javax.servlet.*;
import java.io.IOException;

public class LoginFilter implements Filter {

    UserService userService;

    public LoginFilter() {
        this.userService = new UserServiceImpl();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("filter init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

  /*  @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);

        final HttpServletRequest req = (HttpServletRequest) servletRequest;
        final HttpServletResponse resp = (HttpServletResponse) servletResponse;

        final String login = req.getParameter("login");
        final String password = req.getParameter("password");

        final HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (!session.isNew()
                && nonNull(session.getAttribute("login"))
                && nonNull(session.getAttribute("password"))) {
            System.out.println("you're already logged");
        } else if (user == null) {
            User userDB = userService.getUserByLogin(login);
            if (nonNull(userDB)) {
                if (userDB.getPassword().equals(password)) {
                    session.setAttribute("user", userDB);
                    System.out.printf("User with id %d just signed in", userDB.getId());
                }
            }
        }
    }

   */

    @Override
    public void destroy() {
        System.out.println("filter destroy");
    }
}
