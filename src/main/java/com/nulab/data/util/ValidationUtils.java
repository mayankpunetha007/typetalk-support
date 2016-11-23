package com.nulab.data.util;

import com.nulab.common.InputValidationUtils;
import com.nulab.config.ApplicationConfig;
import com.nulab.data.dto.SupportTicket;
import com.nulab.data.pojo.NewSupportRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayan on 11/18/2016.
 */
@Service("validation")
@ConfigurationProperties
public class ValidationUtils {

    @Autowired
    private InputValidationUtils inputValidationUtils;

    @Autowired
    private ApplicationConfig applicationConfig;


    public List<String> validate(NewSupportRegistration newSupportRegistration){
        List<String> errors = new ArrayList<String>();
        if(!inputValidationUtils.validateEmail(newSupportRegistration.getEmail())){
            errors.add(applicationConfig.getInvalidEmail());
        }
        if(newSupportRegistration.getRequestTopic().length() == 0  || newSupportRegistration.getRequestTopic().length() > 60){
            errors.add(applicationConfig.getInvalidTopicSubject());
        }
        return errors;
    }

    public List<String> validate(SupportTicket supportTicket, String accessToken){
        List<String> errors = new ArrayList<String>();
        if(supportTicket == null){
            errors.add(applicationConfig.getUnauthorized());
        }
        if(!supportTicket.getAccessKey().equals(accessToken)){
            errors.add(applicationConfig.getUnauthorized());
        }
        return errors;
    }

    public InputValidationUtils getInputValidationUtils() {
        return inputValidationUtils;
    }

    public void setInputValidationUtils(InputValidationUtils inputValidationUtils) {
        this.inputValidationUtils = inputValidationUtils;
    }
}
