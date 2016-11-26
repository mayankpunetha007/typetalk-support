package com.nulab.common;

import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Validate user input
 */
@Service("validationHelper")
public class InputValidationUtils {

    public boolean validateEmail(String email, boolean required) {
        if (required) {
            if (email == null || "".equals(email)) {
                return false;
            }
        }
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            return false;
        }
        return true;
    }


}
