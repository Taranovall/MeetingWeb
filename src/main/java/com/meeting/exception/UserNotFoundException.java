package com.meeting.exception;

import java.sql.SQLException;

public class UserNotFoundException extends Exception{

    public UserNotFoundException(String message, SQLException cause) {
        super(message, cause);
    }
}
