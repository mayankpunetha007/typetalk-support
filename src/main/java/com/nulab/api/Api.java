package com.nulab.api;

import com.nulab.api.response.AppApiResponse;
import com.nulab.data.dto.SupportTicket;
import com.nulab.data.pojo.NewSupportRegistration;
import com.nulab.data.service.ApplicationService;
import com.nulab.data.util.ValidationUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mayan on 11/24/2016.
 */
@RestController
public class Api {
    private final Map<String, List> data;
    @Autowired
    private ValidationUtils validationUtils;

    @Autowired
    private ApplicationService applicationService;

    List<Object[]> readData;

    public Api(){
        Test test = new Test();
        readData = test.readData();
        Map<String, List> data = new LinkedHashMap<>();
        for(int i=0;i<readData.get(0).length;i++){
            data.put((String) readData.get(0)[i], new ArrayList());
        }
        for(int i=1;i<readData.size();i++){
            for(int j=0;j<readData.get(i).length;j++) {
                data.get((String) readData.get(0)[j]).add(readData.get(i)[j]);
            }
        }
        this.data = data;

    }

    @ApiOperation(value="/get/data", notes = "Register a new ticket for the given user and send email to the user")
    @RequestMapping(method = RequestMethod.GET, value = "/get/data/{from}/{upto}")
    public AppApiResponse ticket(@PathVariable Long from, @PathVariable Long upto) {
        AppApiResponse apiResponse = new AppApiResponse();
        Map<String, List> values = new HashMap<>();
        for(Map.Entry<String, List> s: data.entrySet()){
            values.put(s.getKey(), s.getValue().subList(0, s.getValue().size()-1));
        }
        apiResponse.setResponse(data);
        return apiResponse;
    }

    @ApiOperation(value="/get/data1", notes = "Register a new ticket for the given user and send email to the user")
    @RequestMapping(method = RequestMethod.GET, value = "/get/data1/{from}/{upto}")
    public AppApiResponse ticket2(@PathVariable Long from, @PathVariable Long upto) {
        AppApiResponse apiResponse = new AppApiResponse();
        apiResponse.setResponse(readData);
        return apiResponse;
    }
}
