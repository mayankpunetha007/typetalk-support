package com.nulab.data.service;

import com.nulab.data.dao.DataAccessService;
import com.nulab.data.dao.EmailSendingService;
import com.nulab.data.dto.SupportTicket;
import com.nulab.data.pojo.NewSupportRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.security.SecureRandom;

/**
 * Created by mayan on 11/19/2016.
 */
@Service("applicationService")
public class ApplicationService {

    @Autowired
    private DataAccessService dataAccessService;

    @Autowired
    private EmailSendingService emailSendingService;

    private static final SecureRandom secureRandom = new SecureRandom();

    public void registerNewServiceRequest(NewSupportRegistration newSupportRegistration) throws MessagingException {
        String email = newSupportRegistration.getEmail();
        String topic = newSupportRegistration.getRequestTopic();
        byte[] bytes = secureRandom.generateSeed(64);
        StringBuffer accessKey = new StringBuffer();
        for (byte b : bytes)
            accessKey.append(String.format("%02X ", b));
        SupportTicket supportTicket = new SupportTicket(newSupportRegistration.getName(), accessKey.toString(), newSupportRegistration.getEmail(), newSupportRegistration.getRequestTopic());
        supportTicket =  dataAccessService.addnewsupportTicket(supportTicket);
        emailSendingService.sendNewSupportEmail(supportTicket.getId(), supportTicket.getAccessKey());
    }
}
