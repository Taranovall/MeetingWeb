package com.meeting.exception;

import javax.mail.MessagingException;

public class EmailException extends MessagingException {

    public EmailException(String message, Exception exception) {
        super(message, exception);
    }
}
