package com.nulab.data.service;

import com.nulab.api.response.AppApiResponse;
import com.nulab.config.ApplicationConfig;
import com.nulab.data.dao.ChatDetailsDao;
import com.nulab.data.dao.ExternalDataDao;
import com.nulab.data.dao.SupportTicketDao;
import com.nulab.data.dto.ChatDetails;
import com.nulab.data.dto.SupportTicket;
import com.nulab.data.pojo.NewSupportRegistration;
import com.nulab.data.pojo.inner.Topic;
import com.nulab.data.typetalk.TypeTalkService;
import com.nulab.data.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

/**
 * Main application related servcies
 */
@Service("applicationService")
public class ApplicationService {

    private static final SecureRandom secureRandom = new SecureRandom();
    @Autowired
    ExternalDataDao externalDataDao;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    private SupportTicketDao supportTicketDao;
    @Autowired
    private ApplicationConfig applicationConfig;
    @Autowired
    private ValidationUtils validationUtils;
    @Autowired
    private TypeTalkService typeTalkService;
    @Autowired
    private ChatDetailsDao chatDetailsDao;

    /**
     * Convert bytes to Hexadecimal String
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Register a user for a new support Ticket
     * @param newSupportRegistration
     * @throws MessagingException
     */
    public SupportTicket registerNewServiceRequest(NewSupportRegistration newSupportRegistration) throws MessagingException {
        byte[] bytes = secureRandom.generateSeed(64);
        String accessKey = bytesToHex(bytes);
        SupportTicket supportTicket = new SupportTicket(newSupportRegistration.getName(), accessKey, newSupportRegistration.getEmail(), "SUPPORT:"+newSupportRegistration.getRequestTopic());
        supportTicket = supportTicketDao.save(supportTicket);
        if(applicationConfig.isConfirmViaMail()) {
            emailSendingService.sendNewSupportEmail(supportTicket.getUserMail(), supportTicket.getId(), supportTicket.getAccessKey());
            return null;
        }else{
            return supportTicket;
        }
    }

    /**
     * Send messafe to the group assigned to the Customer based on the assigned customer id and authorization key
     * @param id
     * @param accessToken
     * @param message
     * @return
     */
    @Transactional
    public AppApiResponse sendMessageToGroup(Long id, String accessToken, String message) {
        AppApiResponse appApiResponse = new AppApiResponse();
        List<String> errorMessages = new ArrayList<>();
        SupportTicket supportTicket = supportTicketDao.findOne(id);
        List<String> errors = validationUtils.validate(supportTicket, accessToken);
        if (errors.size() != 0) {
            appApiResponse.setErrorMessages(errors);
            return appApiResponse;
        }
        Topic topic = new Topic();
        topic.setId(supportTicket.getTopicId());
        try {
            message = typeTalkService.postMessageToTopic(topic, message);
        } catch (IOException e) {
            errorMessages.add("Could not post message");
            appApiResponse.setErrorMessages(errorMessages);
        }
        return appApiResponse;


    }

    /**
     * Validate the user details and start a new support ticket for the user
     * This will create the requested topic for the user
     * @param id
     * @param accessToken
     * @return
     */
    public AppApiResponse startNewSupport(Long id, String accessToken) {
        AppApiResponse<Map> appApiResponse = new AppApiResponse<>();

        SupportTicket supportTicket = supportTicketDao.findOne(id);
        List<String> errors = validationUtils.validate(supportTicket, accessToken);
        if (errors.size() > 0) {
            appApiResponse.setErrorMessages(errors);
            return appApiResponse;
        }
        try {
            List<ChatDetails> chatDetails = new ArrayList<>();
            if (supportTicket.getTopicId() == null) {
                Topic topic = typeTalkService.createTopic(supportTicket.getTopic());
                supportTicket.setTopicId(topic.getId());
                supportTicket = supportTicketDao.save(supportTicket);
            } else {
                chatDetails = chatDetailsDao.findAllBySupportTicket(supportTicket);
                chatDetails.forEach(m -> m.setSupportTicket(null));
            }
            Map<String, Object> result = new HashMap<>();
            result.put("supportDetails", supportTicket);
            result.put("chatDetails", chatDetails);
            appApiResponse.setResponse(result);

        } catch (IOException e) {
            errors.add("Sorry we could not Create your support ticket Now. Please re register");
            appApiResponse.setErrorMessages(errors);
        }
        return appApiResponse;
    }
}
