package com.nulab.api;

import com.nulab.data.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by mayan on 11/18/2016.
 */
@Controller
public class HtmlServer {

    @Autowired
    ApplicationService applicationService;

    @RequestMapping({"/", "/index", "/index.html", "/index.jsp", "/home", "/home.html", "/home.jsp"})
    public String index() {
        return "html/index.html";
    }

    @RequestMapping({"/success", "/success.html", "/success.jsp", "/success.php"})
    public String success() {
        return "html/succesreg.html";
    }

    @RequestMapping({"/failure", "/failure.html", "/failure.jsp", "/failure.php"})
    public String failure() {
        return "html/failreg.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/support/{id}/{accessToken}")
    public String start(@PathVariable Long id, @PathVariable String accessToken) {
        return "html/chat.html";
    }


}
