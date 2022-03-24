package com.meeting.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

@WebFilter(filterName = "charset", urlPatterns = "/*")
public class CharsetFilter implements Filter {
    private String encoding;

    public void init(FilterConfig config) {
        encoding = config.getInitParameter("requestEncoding");

        if (encoding == null) encoding = "UTF-8";
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (null == servletRequest.getCharacterEncoding())
            servletRequest.setCharacterEncoding(encoding);

        //Set the default response content type and encoding
        servletResponse.setContentType("text/html; charset=UTF-8");
        servletResponse.setCharacterEncoding("UTF-8");

        ServletContext ctx = servletRequest.getServletContext();
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpSession session = req.getSession();

        String currentLanguage = (String) session.getAttribute("language");

        if (Objects.isNull(currentLanguage)) {
            String defaultLanguage = ctx.getInitParameter("default-language");
            session.setAttribute("language", defaultLanguage);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }
}
