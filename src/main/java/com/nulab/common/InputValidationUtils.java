package com.nulab.common;

import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Created by mayan on 11/18/2016.
 */
@Service("validationHelper")
public class InputValidationUtils {

    public boolean validateEmail(String email){
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            return false;
        }
        return true;
    }


}
