package com.nulab.data.util;

import com.nulab.common.InputValidationUtils;
import com.nulab.config.ApplicationConfig;
import com.nulab.data.dao.SupportTicketDao;
import com.nulab.data.dto.SupportTicket;
import com.nulab.data.pojo.NewSupportRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

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

    @Autowired
    private SupportTicketDao supportTicketDao;


    /**
     * Validate NewSupportRegistration request
     * @param newSupportRegistration
     * @return
     */
    public List<String> validate(NewSupportRegistration newSupportRegistration) {
        List<String> errors = new ArrayList<>();
        if (!inputValidationUtils.validateEmail(newSupportRegistration.getEmail(), true)) {
            errors.add(applicationConfig.getInvalidEmail());
        }
        if (newSupportRegistration.getRequestTopic().length() == 0 || newSupportRegistration.getRequestTopic().length() > 60) {
            errors.add(applicationConfig.getInvalidTopicSubject());
        }
        if (supportTicketDao.findByTopic(newSupportRegistration.getRequestTopic()) != null) {
            errors.add("Topic Already exists");
        }
        return errors;
    }

    /**
     * Validate if the given accessToken is valid
     * @param supportTicket
     * @param accessToken
     * @return
     */
    public List<String> validate(SupportTicket supportTicket, String accessToken) {
        List<String> errors = new ArrayList<>();
        if (supportTicket == null) {
            errors.add(applicationConfig.getUnauthorized());
            return errors;
        }
        if (!supportTicket.getAccessKey().equals(accessToken)) {
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
