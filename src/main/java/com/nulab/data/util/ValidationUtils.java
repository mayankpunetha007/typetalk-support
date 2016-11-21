package com.nulab.data.util;

import com.nulab.common.InputValidationUtils;
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

    public static String invalidTopicSubject;

    public static String invalidEmail;


    public List<String> validate(NewSupportRegistration newSupportRegistration){
        List<String> errors = new ArrayList<String>();
        if(!inputValidationUtils.validateEmail(newSupportRegistration.getEmail())){
            errors.add(invalidEmail);
        }
        if(newSupportRegistration.getRequestTopic().length() == 0  || newSupportRegistration.getRequestTopic().length() > 60){
            errors.add(invalidTopicSubject);
        }
        return errors;
    }

    public InputValidationUtils getInputValidationUtils() {
        return inputValidationUtils;
    }

    public void setInputValidationUtils(InputValidationUtils inputValidationUtils) {
        this.inputValidationUtils = inputValidationUtils;
    }

    public static String getInvalidTopicSubject() {
        return invalidTopicSubject;
    }

    public static void setInvalidTopicSubject(String invalidTopicSubject) {
        ValidationUtils.invalidTopicSubject = invalidTopicSubject;
    }

    public static String getInvalidEmail() {
        return invalidEmail;
    }

    public static void setInvalidEmail(String invalidEmail) {
        ValidationUtils.invalidEmail = invalidEmail;
    }
}
