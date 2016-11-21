package com.nulab.api.registration;

import com.nulab.api.response.AppApiResponse;
import com.nulab.data.dto.SupportTicket;
import com.nulab.data.pojo.NewSupportRegistration;
import com.nulab.data.service.ApplicationService;
import com.nulab.data.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by mayan on 11/18/2016.
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/register")
public class Registeration {

    @Autowired
    private ValidationUtils validationUtils;

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(method = RequestMethod.POST, value = "/ticket")
    public AppApiResponse ticket(@RequestBody NewSupportRegistration newSupportRegistration) {
        List<String> errrors = validationUtils.validate(newSupportRegistration);
        AppApiResponse<String> appApiResponse = new AppApiResponse<String>();
        if(errrors.size() == 0){
            try {
            applicationService.registerNewServiceRequest(newSupportRegistration);
            appApiResponse.setResponse("success");
            applicationService.registerNewServiceRequest(newSupportRegistration);
            }catch (Exception e){
                errrors.add("System Error");
                appApiResponse.setErrorMessages(errrors);
            }
        }else{
            appApiResponse.setErrorMessages(errrors);
        }
        return appApiResponse;
    }
}
