package com.nulab.data.dao;

import com.nulab.config.ApplicationConfig;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by mayan on 11/20/2016.
 */
@Service("emailSendingService")
@ConfigurationProperties
public class EmailSendingService {

    Properties props = new Properties();

    private String gmailId;

    private String gmailPassword;

    private Properties properties;

    private Session session;

    private Logger logger = Logger.getLogger(EmailSendingService.class);

    public EmailSendingService(){
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imaps.host", "smtp.gmail.com");
        props.setProperty("mail.imaps.port", "");

        props.setProperty("mail.imaps.starttls.enable", "true");
        props.setProperty("mail.protocol.ssl.trust", "smtp.gmail.com");
        props.setProperty("mail.imaps.socketFactory.fallback", "false");
        props.setProperty("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(gmailId, gmailPassword);
            }
        });
    }

        public void sendNewSupportEmail(Long userId, String accessCode) throws MessagingException {

            logger.info("Recieved request to send mail");

            String url = String.format("%s/%s/%s", ApplicationConfig.baseUrl, userId, accessCode);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from-email@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("to-email@gmail.com"));
            message.setSubject("Thank you for contacting Support");
            message.setText(String.format("Please follow the link to continue or start your support time %s", url));
            Transport.send(message);

            logger.info("Email was successfully sent");

        }
}
