package com.nulab.api;

import com.nulab.api.response.AppApiResponse;
import com.nulab.data.pojo.NewSupportRegistration;
import com.nulab.data.service.ApplicationService;
import com.nulab.data.util.ValidationUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by mayan on 11/24/2016.
 */
@RestController
public class Api {
    @Autowired
    private ValidationUtils validationUtils;

    @Autowired
    private ApplicationService applicationService;

    @ApiOperation(value="/register/ticket", notes = "Register a new ticket for the given user and send email to the user")
    @RequestMapping(method = RequestMethod.POST, value = "/register/ticket", consumes = "application/json")
    public AppApiResponse ticket(@RequestBody NewSupportRegistration newSupportRegistration) {
        List<String> errrors = validationUtils.validate(newSupportRegistration);
        AppApiResponse<String> appApiResponse = new AppApiResponse<>();
        if (errrors.size() == 0) {
            try {
                applicationService.registerNewServiceRequest(newSupportRegistration);
                appApiResponse.setResponse("success");
            } catch (Exception e) {
                errrors.add("System Error");
                appApiResponse.setErrorMessages(errrors);
            }
        } else {
            appApiResponse.setErrorMessages(errrors);
        }
        return appApiResponse;
    }

    @ApiOperation(value="/messages/{id}/{accessToken}", notes = "Get all messages in the topic identified by given user and authorized by given accessTOken")
    @RequestMapping(method = RequestMethod.POST, value = "/messages/{id}/{accessToken}")
    public AppApiResponse sendToSupport(@PathVariable Long id, @PathVariable String accessToken) {
        return applicationService.startNewSupport(id, accessToken);
    }
}
