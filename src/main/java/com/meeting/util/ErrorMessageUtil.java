package com.meeting.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.ResourceBundle;

public class ErrorMessageUtil {

    public static String getByLocale(HttpServletRequest req, String error) {
        Locale locale = new Locale((String) req.getSession().getAttribute("language"));
        return ResourceBundle.getBundle("resources", locale).getString(error);
    }
}
