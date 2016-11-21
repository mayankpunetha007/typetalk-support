package com.nulab.server.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nulab.data.pojo.NewSupportRegistration;

/**
 * Created by mayan on 11/20/2016.
 */
public class Main {

    public static void main(String[] args) throws JsonProcessingException {
        NewSupportRegistration newSupportRegistration = new NewSupportRegistration();
        newSupportRegistration.setName("Mayank Punetha");
        newSupportRegistration.setEmail("coder.mayank.punetha@gmail.com");
        newSupportRegistration.setRequestTopic("Hello");
        System.out.print((new ObjectMapper()).writeValueAsString(newSupportRegistration));
    }
}
