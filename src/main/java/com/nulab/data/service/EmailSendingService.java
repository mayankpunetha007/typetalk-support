package com.nulab.data.service;

import com.nulab.config.ApplicationConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Service to send authorization mail to the user
 */
@Service("emailSendingService")
@ConfigurationProperties
public class EmailSendingService {

    @Autowired
    private ApplicationConfig applicationConfig;

    private Properties props;

    private Session session;

    private Logger logger = Logger.getLogger(EmailSendingService.class);

    public EmailSendingService() {
        props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(applicationConfig.getGmailId(), applicationConfig.getGmailPassword());
            }
        });
    }

    @PostConstruct
    void init() {
        props.put("mail.smtp.user", applicationConfig.getGmailId());
        props.put("mail.smtp.password", applicationConfig.getGmailPassword());
    }

    /**
     * Send activation and support group url mail to the given email Id with the given user id and accesCode
     * Please ensure that baseurl is correct in application.properties to make sure the url sent to the user
     * is correct
     * @param email
     * @param userId
     * @param accessCode
     * @throws MessagingException
     */

    public void sendNewSupportEmail(String email, Long userId, String accessCode) throws MessagingException {

        logger.info("Recieved request to send mail");

        String url = String.format("%s/%s/%s/%s", ApplicationConfig.baseUrl, "support", userId, accessCode);
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(applicationConfig.getGmailId()));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(email));
        message.setSubject("Thank you for contacting Support");
        message.setText(String.format("Please follow the link to continue or start your support time %n%s", url));
        Transport.send(message);

        logger.info("Email was successfully sent");

    }
}
