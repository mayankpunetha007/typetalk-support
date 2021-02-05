package com.nulab.data.service;

import com.nulab.api.response.AppApiResponse;
import com.nulab.config.ApplicationConfig;
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
    private EmailSendingService emailSendingService;
    @Autowired
    private ApplicationConfig applicationConfig;
    @Autowired
    private ValidationUtils validationUtils;
    @Autowired
    private TypeTalkService typeTalkService;

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
        return null;
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
        return appApiResponse;
    }
}
