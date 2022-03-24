package com.meeting.filter;

import com.meeting.entitiy.User;
import com.meeting.service.UserService;
import com.meeting.service.impl.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@WebFilter(filterName = "loginFilet", urlPatterns = "/login")
public class LoginFilter implements Filter {

    private static final Logger log = LogManager.getLogger(LoginFilter.class);
    UserService userService;

    public LoginFilter() {
        this.userService = new UserServiceImpl();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Login filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        User user = (User) req.getSession().getAttribute("user");
        if (Objects.isNull(user)) {
            filterChain.doFilter(req, resp);
        } else {
            String lastUri = (String) req.getSession().getAttribute("lastPageURI");
            resp.sendRedirect(lastUri);
        }
    }

    @Override
    public void destroy() {
        log.info("Login filter destroy");
    }
}
