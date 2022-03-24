package com.meeting.filter;

import com.meeting.entitiy.Role;
import com.meeting.entitiy.User;
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

@WebFilter(filterName = "moderatorFilter", urlPatterns = "/moderator/*")
public class ModeratorFilter implements Filter {

    private static final Logger log = LogManager.getLogger(ModeratorFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Moderator filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        User user = (User) req.getSession().getAttribute("user");
        if (user != null && user.getRole().equals(Role.MODERATOR)) {
            chain.doFilter(req, resp);
        } else {
            String lastUri = (String) req.getSession().getAttribute("lastPageURI");
            resp.sendRedirect(lastUri);
        }
    }

    @Override
    public void destroy() {
        log.info("Moderator filter destroy");
    }
}
