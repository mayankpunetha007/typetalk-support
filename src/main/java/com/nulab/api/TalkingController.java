package com.nulab.api;

import com.nulab.api.response.AppApiResponse;
import com.nulab.data.dto.ExternalData;
import com.nulab.data.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by mayan on 11/24/2016.
 */
@Controller
public class TalkingController {

    private static Logger logger = LoggerFactory.getLogger(TalkingController.class);

    @Autowired
    private ApplicationService applicationService;

    @SendTo("")
    public AppApiResponse publishToClient(@PathVariable Long id, @PathVariable String accessToken, @RequestBody ExternalData externalData) {
        logger.info("Received message: /" + id + "/" + accessToken);
        AppApiResponse<ExternalData> appApiResponse = new AppApiResponse<>();
        appApiResponse.setResponse(externalData);
        return appApiResponse;
    }

   /* @MessageMapping("/talk/m")
    AppApiResponse getFromClient(@DestinationVariable Long id, @DestinationVariable String accessToken) {
        SupportTicket supportTicket = supportTicketDao.findOne(id);
        return applicationService.sendMessageToGroup(supportTicket.getTopicId(), accessToken, "");
    }*/

    @MessageMapping(value = "/talk/{id}/{accessToken}")
    @SendTo(value = "/talk/{id}/{accessToken}")
    public String simple(@DestinationVariable Long id, @DestinationVariable String accessToken) {
        logger.info(id+accessToken);
        return id + accessToken;
    }


}
