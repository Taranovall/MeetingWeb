package com.meeting.exception;

import java.sql.SQLException;

public class DataBaseException extends Exception {

    public DataBaseException(String message, SQLException cause) {
        super(message, cause);
    }
}
