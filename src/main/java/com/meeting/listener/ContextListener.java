package com.meeting.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        String localesFileName = ctx.getInitParameter("locales");
        String logFileName = ctx.getInitParameter("logger");
        // obtain real path on server
        String logFileRealPath = ctx.getRealPath(logFileName);
        String localesFileRealPath = ctx.getRealPath(localesFileName);

        System.setProperty("logFile", logFileRealPath);

        // load descriptions
        Properties locales = new Properties();
        try {
            locales.load(new FileInputStream(localesFileRealPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ctx.setAttribute("locales", locales);
        locales.list(System.out);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
