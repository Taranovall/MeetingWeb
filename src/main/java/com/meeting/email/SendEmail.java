package com.meeting.email;

import com.meeting.exception.EmailException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class SendEmail {

    private static final Logger log = LogManager.getLogger(SendEmail.class);

    private Message message = null;
    protected static String SMTP_SERVER = "smtp.gmail.com";
    protected static String SMTP_PORT = "587";
    protected static String SMTP_AUTH_USER = "meetingwebmailsender@gmail.com";
    protected static String SMTP_AUTH_PWD = "g1872qwe3";
    protected static String EMAIL_FROM = "MeetingWebSender@gmail.com";

    public SendEmail(final String[] emailTo, final String topic) throws EmailException {
        // Configuring SMTP SSL
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_SERVER);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.ssl.trust", SMTP_SERVER);
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        try {
            Authenticator auth = new EmailAuthenticator(SMTP_AUTH_USER,
                    SMTP_AUTH_PWD);
            Session session = Session.getDefaultInstance(properties, auth);
            session.setDebug(false);

            InternetAddress email_from = new InternetAddress(EMAIL_FROM);
            InternetAddress[] email_to = fillArrayWithAddresses(emailTo);
            message = new MimeMessage(session);
            message.setFrom(email_from);
            message.setRecipients(Message.RecipientType.TO, email_to);
            message.setSubject(topic);
        } catch (MessagingException e) {
            log.error("Cannot initialize constructor", e);
            throw new EmailException("Cannot initialize constructor", e);
        }
    }

    /**
     *
     * @param text message's text
     * @return true if the function was successful
     */
    public boolean sendMessage(final String text) throws EmailException {
        try {
            // message content
            Multipart mmp = new MimeMultipart();
            // message text
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(text, "text/plain; charset=utf-8");
            mmp.addBodyPart(bodyPart);
            message.setContent(mmp);
            // sending message
            Transport.send(message);
            log.info("Email with text :\n{} \nwas sent to {} users", text, message.getAllRecipients().length);
        } catch (MessagingException e) {
            log.error("Cannot send message with text:\n {}", text, e);
            String message = String.format("Cannot send message with text %s", text);
            throw new EmailException(message, e);

        }
        return true;
    }

    /**
     *
     * @param emailTo string array with email addresses
     * @return InternetAddress array with addresses from {@code emailTo}
     */
    private InternetAddress[] fillArrayWithAddresses(String[] emailTo) throws AddressException {
        InternetAddress[] internetAddresses = new InternetAddress[emailTo.length];
        for (int i = 0; i < emailTo.length; i++) {
            internetAddresses[i] = new InternetAddress(emailTo[i]);
        }
        return internetAddresses;
    }
}