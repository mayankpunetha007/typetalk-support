package com.nulab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mayan on 11/20/2016.
 */
@Component
@ConfigurationProperties
@Configuration
public class ApplicationConfig {

    public String invalidTopicSubject;

    public String invalidEmail;

    public String gmailId;

    public String gmailPassword;

    public String unauthorized;

    private String typetalkClientId;

    private String typetalkClientSecret;

    private List<String> typetalkSupportGroups;

    private List<String> typetalkSupportAccountId;

    private String typetalkOrganisation;

    public String baseUrl;

    public static boolean isSupportAccount(Long id) {
        return false;
    }

    public static boolean isGuestAccount(Long id) {
        return false;
    }

    public String getTypetalkClientId() {
        return typetalkClientId;
    }

    public void setTypetalkClientId(String typetalkClientId) {
        this.typetalkClientId = typetalkClientId;
    }

    public String getTypetalkClientSecret() {
        return typetalkClientSecret;
    }

    public void setTypetalkClientSecret(String typetalkClientSecret) {
        this.typetalkClientSecret = typetalkClientSecret;
    }

    public void setTypetalkSupportGroups(List<String> typetalkSupportGroups) {
        this.typetalkSupportGroups = typetalkSupportGroups;
    }

    public void setTypetalkSupportAccountId(List<String> typetalkSupportAccountId) {
        this.typetalkSupportAccountId = typetalkSupportAccountId;
    }

    public List<String> getTypetalkSupportGroups() {
        return typetalkSupportGroups;
    }

    public void setTypettalkSupportGroups(String typettalkSupportGroups) {
        this.typetalkSupportGroups = Arrays.asList(typettalkSupportGroups.split(","));
    }

    public String getTypetalkOrganisation() {
        return typetalkOrganisation;
    }

    public void setTypetalkOrganisation(String typetalkOrganisation) {
        this.typetalkOrganisation = typetalkOrganisation;
    }

    public List<String> getTypetalkSupportAccountId() {
        return typetalkSupportAccountId;
    }

    public void setTypettalkSupportAccountId(String typettalkSupportAccountId) {
        this.typetalkSupportAccountId = Arrays.asList(typettalkSupportAccountId.split(","));
    }

    public String getInvalidTopicSubject() {
        return invalidTopicSubject;
    }

    public void setInvalidTopicSubject(String invalidTopicSubject) {
        this.invalidTopicSubject = invalidTopicSubject;
    }

    public String getInvalidEmail() {
        return invalidEmail;
    }

    public void setInvalidEmail(String invalidEmail) {
        this.invalidEmail = invalidEmail;
    }

    public String getGmailId() {
        return gmailId;
    }

    public void setGmailId(String gmailId) {
        this.gmailId = gmailId;
    }

    public String getGmailPassword() {
        return gmailPassword;
    }

    public void setGmailPassword(String gmailPassword) {
        this.gmailPassword = gmailPassword;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUnauthorized() {
        return unauthorized;
    }

    public void setUnauthorized(String unauthorized) {
        this.unauthorized = unauthorized;
    }
}

