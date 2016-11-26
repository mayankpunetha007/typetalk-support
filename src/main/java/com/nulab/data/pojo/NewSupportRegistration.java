package com.nulab.data.pojo;

/**
 * Created by mayan on 11/18/2016.
 */
public class NewSupportRegistration {

    private String name;
    private String email;
    private String requestTopic;

    public NewSupportRegistration() {
    }

    public NewSupportRegistration(String name, String email, String requestTopic) {
        this.name = name;
        this.email = email;
        this.requestTopic = requestTopic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRequestTopic() {
        return requestTopic;
    }

    public void setRequestTopic(String requestTopic) {
        this.requestTopic = requestTopic;
    }
}
