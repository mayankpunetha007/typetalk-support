package com.nulab.api.registration;

import com.nulab.api.response.AppApiResponse;
import com.nulab.data.dto.SupportTicket;
import com.nulab.data.pojo.NewSupportRegistration;
import com.nulab.data.service.ApplicationService;
import com.nulab.data.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;

/**
 * Created by mayan on 11/18/2016.
 */
@RestController
@EnableAutoConfiguration
public class Api {

    @Autowired
    private ValidationUtils validationUtils;

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(method = RequestMethod.POST, value = "/register/ticket", consumes = "application/json")
    public AppApiResponse ticket(@RequestBody NewSupportRegistration newSupportRegistration) {
        List<String> errrors = validationUtils.validate(newSupportRegistration);
        AppApiResponse<String> appApiResponse = new AppApiResponse<String>();
        if(errrors.size() == 0){
            try {
            applicationService.registerNewServiceRequest(newSupportRegistration);
            appApiResponse.setResponse("success");
            }catch (Exception e){
                errrors.add("System Error");
                appApiResponse.setErrorMessages(errrors);
            }
        }else{
            appApiResponse.setErrorMessages(errrors);
        }
        return appApiResponse;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/support/{id}/{accessToken}")
    public AppApiResponse beginSupport(@PathVariable Long id, @PathVariable String accessToken)  {
        return applicationService.startNewSupport(id, accessToken);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/message/{id}/{accessToken}")
    public AppApiResponse sendToSupport(@PathVariable Long id, @PathVariable String accessToken, @RequestBody String message)  {
        return applicationService.sendMessageToGroup(id, accessToken, message);
    }
}
