package com.meeting.email;

import com.meeting.exception.EmailException;
import org.junit.jupiter.api.Test;

import javax.mail.Message;
import javax.mail.MessagingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class SendEmailTest {

    @Test
    void shouldThrowMessagingExceptionWithMessage_CannotSendMessage() throws MessagingException {
        Message message = mock(Message.class);
        doThrow(MessagingException.class).when(message).setContent(any());

        String[] emailTo = {"_email1@gmail.com", "_email2@gmail.com"};
        String topic = "test";
        SendEmail sendEmail = new SendEmail(emailTo, topic);

        sendEmail.setMessage(message);

        EmailException thrown = assertThrows(EmailException.class, () -> sendEmail.sendMessage("TestMsg"));

        String expected = "Cannot send message with text TestMsg";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowMessagingExceptionWithMessage_CannotInitializeConstructor() {

        String[] emailTo = {"_email1@gmail.com", "_email2@gmail.com"};
        String topic = "test";

        SendEmail.MAIL_SMTP_SSL_PROTOCOLS = null;

        EmailException thrown = assertThrows(EmailException.class, () -> new SendEmail(emailTo, topic));

        SendEmail.MAIL_SMTP_SSL_PROTOCOLS = "TLSv1.2";

        String expected = "Cannot initialize constructor";
        String actual = thrown.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void shouldSendMessageAndReturnTrue() throws EmailException {
        String[] emailTo = {"_email1@gmail.com", "_email2@gmail.com"};
        String topic = "test";

        SendEmail sendEmail = new SendEmail(emailTo, topic);
        boolean result = sendEmail.sendMessage("TestMsg");

        assertTrue(result);
    }
}