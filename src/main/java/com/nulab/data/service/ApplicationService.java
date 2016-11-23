package com.nulab.data.service;

import com.nulab.api.response.AppApiResponse;
import com.nulab.config.ApplicationConfig;
import com.nulab.data.dao.ChatDetailsDao;
import com.nulab.data.dao.ExternalDataDao;
import com.nulab.data.dao.SupportTicketDao;
import com.nulab.data.dto.ChatDetails;
import com.nulab.data.dto.ExternalData;
import com.nulab.data.dto.SupportTicket;
import com.nulab.data.pojo.NewSupportRegistration;
import com.nulab.data.pojo.inner.Account;
import com.nulab.data.pojo.inner.Topic;
import com.nulab.data.typetalk.TypeTalkService;
import com.nulab.data.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mayan on 11/19/2016.
 */
@Service("applicationService")
public class ApplicationService {

    @Autowired
    private EmailSendingService emailSendingService;

    @Autowired
    ExternalDataDao externalDataDao;

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


    public void addDataIfImp(Topic topic, Account account, String message) {
        if(ApplicationConfig.isSupportAccount(account.getId()) || ApplicationConfig.isGuestAccount(account.getId())) {
            ExternalData externalData = new ExternalData();
            externalData.setAccountId(account.getId());
            externalData.setTopicId(topic.getId());
            externalData.setWatched(false);
            externalData.setSupport(ApplicationConfig.isSupportAccount(account.getId()));
            externalData.setCreationTime(new Date(System.currentTimeMillis()));
            externalDataDao.save(externalData);
        }

    }

    private static final SecureRandom secureRandom = new SecureRandom();

    public void registerNewServiceRequest(NewSupportRegistration newSupportRegistration) throws MessagingException {
        String email = newSupportRegistration.getEmail();
        String topic = newSupportRegistration.getRequestTopic();
        byte[] bytes = secureRandom.generateSeed(64);
        String accessKey = bytesToHex(bytes);
        SupportTicket supportTicket = new SupportTicket(newSupportRegistration.getName(), accessKey, newSupportRegistration.getEmail(), newSupportRegistration.getRequestTopic());
        supportTicket =  supportTicketDao.save(supportTicket);
        emailSendingService.sendNewSupportEmail(supportTicket.getUserMail(), supportTicket.getId(), supportTicket.getAccessKey());
    }

    @Transactional
    public AppApiResponse sendMessageToGroup(Long id, String accessToken, String message) {
        AppApiResponse appApiResponse = new AppApiResponse();
        List<String> errorMessages = new ArrayList<>();
        SupportTicket supportTicket = supportTicketDao.findOne(id);
        List<String> errors = validationUtils.validate(supportTicket, accessToken);
        if(errors.size()!=0){
            appApiResponse.setErrorMessages(errors);
            return appApiResponse;
        }
        Topic topic = new Topic();
        topic.setId(supportTicket.getTopicId());
        try {
            ChatDetails chatDetails = new ChatDetails();
            chatDetails.setSupport(false);
            chatDetails.setChatContent(message);
            chatDetails.setSupportTicket(supportTicket);
            chatDetails.setTime(new Date(System.currentTimeMillis()));
            chatDetailsDao.save(chatDetails);
            message = typeTalkService.postMessageToTopic(topic, message);
        }catch (IOException e){
            errorMessages.add("Could not post message");
            appApiResponse.setErrorMessages(errorMessages);
        }
        return appApiResponse;


    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    public AppApiResponse startNewSupport(Long id, String accessToken) {
        AppApiResponse appApiResponse = new AppApiResponse();

        SupportTicket supportTicket = supportTicketDao.findOne(id);
        List<String> errors = validationUtils.validate(supportTicket, accessToken);
        if(errors.size() > 0)
            appApiResponse.setErrorMessages(errors);
        try {
            if(supportTicket.getTopicId() == null) {
                Topic topic = typeTalkService.createTopic(supportTicket.getTopic());
                supportTicket.setTopicId(topic.getId());
                supportTicket = supportTicketDao.save(supportTicket);
                appApiResponse.setResponse("Success");
            }else{
                appApiResponse.setResponse("Already Exists");
            }
        }catch (IOException e){
            errors.add("Sorry we could not Create your support ticket Now. Please re register");
            appApiResponse.setErrorMessages(errors);
        }
        return appApiResponse;
    }
}
