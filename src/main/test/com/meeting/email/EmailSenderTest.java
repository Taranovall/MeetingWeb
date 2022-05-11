package com.meeting.email;

import com.meeting.exception.EmailException;
import org.junit.jupiter.api.Test;

import javax.mail.Message;
import javax.mail.MessagingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class EmailSenderTest {

    private static final String[] EMAIL_TO = {"_email1@gmail.com", "_email2@gmail.com"};
    private static final String TOPIC_NAME = "test";

    @Test
    void shouldThrowMessagingExceptionWithMessage_CannotSendMessage() throws MessagingException {
        Message message = mock(Message.class);
        doThrow(MessagingException.class).when(message).setContent(any());

        EmailSender emailSender = new EmailSender(EMAIL_TO, TOPIC_NAME);

        emailSender.setMessage(message);

        EmailException thrown = assertThrows(EmailException.class, () -> emailSender.sendMessage("TestMsg"));

        String expected = "Cannot send message with text TestMsg";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowMessagingExceptionWithMessage_CannotInitializeConstructor() {
        EmailSender.MAIL_SMTP_SSL_PROTOCOLS = null;

        EmailException thrown = assertThrows(EmailException.class, () -> new EmailSender(EMAIL_TO, TOPIC_NAME));

        EmailSender.MAIL_SMTP_SSL_PROTOCOLS = "TLSv1.2";

        String expected = "Cannot initialize constructor";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldSendMessageAndReturnTrue() throws EmailException {
        EmailSender emailSender = new EmailSender(EMAIL_TO, TOPIC_NAME);
        boolean result = emailSender.sendMessage("TestMsg");

        assertTrue(result);
    }
}