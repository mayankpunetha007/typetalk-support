package com.nulab.config;

import com.nulab.data.pojo.inner.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Arrays;
import java.util.List;

/**
 * Read all properties from application.properties
 * And initialize Web Configuration
 */
@Component
@ConfigurationProperties
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
public class ApplicationConfig extends WebMvcConfigurerAdapter {

    private static String invalidTopicSubject;

    private static String invalidEmail;

    private static String gmailId;

    private static String gmailPassword;

    private static String unauthorized;

    public static String baseUrl;

    private static String typetalkClientId;

    private static String typetalkClientSecret;

    private static List<String> typetalkSupportGroups;

    private static List<String> typetalkSupportAccountId;

    private static String typetalkOrganisation;

    private static Account myAccount;

    private static boolean confirmViaMail = true;

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public String getTypetalkClientId() {
        return typetalkClientId;
    }

    public void setTypetalkClientId(String typetalkClientId) {
        ApplicationConfig.typetalkClientId = typetalkClientId;
    }

    public String getTypetalkClientSecret() {
        return typetalkClientSecret;
    }

    public void setTypetalkClientSecret(String typetalkClientSecret) {
        ApplicationConfig.typetalkClientSecret = typetalkClientSecret;
    }

    public static Account getMyAccount() {
        return myAccount;
    }

    public static void setMyAccount(Account myAccount) {
        ApplicationConfig.myAccount = myAccount;
    }

    public List<String> getTypetalkSupportGroups() {
        return typetalkSupportGroups;
    }

    public void setTypetalkSupportGroups(List<String> typetalkSupportGroups) {
        ApplicationConfig.typetalkSupportGroups = typetalkSupportGroups;
    }

    public void setTypettalkSupportGroups(String typettalkSupportGroups) {
        typetalkSupportGroups = Arrays.asList(typettalkSupportGroups.split(","));
    }

    public String getTypetalkOrganisation() {
        return typetalkOrganisation;
    }

    public void setTypetalkOrganisation(String typetalkOrganisation) {
        ApplicationConfig.typetalkOrganisation = typetalkOrganisation;
    }

    public List<String> getTypetalkSupportAccountId() {
        return typetalkSupportAccountId;
    }

    public void setTypetalkSupportAccountId(List<String> typetalkSupportAccountId) {
        ApplicationConfig.typetalkSupportAccountId = typetalkSupportAccountId;
    }

    public void setTypettalkSupportAccountId(String typettalkSupportAccountId) {
        typetalkSupportAccountId = Arrays.asList(typettalkSupportAccountId.split(","));
    }

    public String getInvalidTopicSubject() {
        return invalidTopicSubject;
    }

    public void setInvalidTopicSubject(String invalidTopicSubject) {
        ApplicationConfig.invalidTopicSubject = invalidTopicSubject;
    }

    public String getInvalidEmail() {
        return invalidEmail;
    }

    public void setInvalidEmail(String invalidEmail) {
        ApplicationConfig.invalidEmail = invalidEmail;
    }

    public String getGmailId() {
        return gmailId;
    }

    public void setGmailId(String gmailId) {
        ApplicationConfig.gmailId = gmailId;
    }

    public String getGmailPassword() {
        return gmailPassword;
    }

    public void setGmailPassword(String gmailPassword) {
        ApplicationConfig.gmailPassword = gmailPassword;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        ApplicationConfig.baseUrl = baseUrl;
    }

    public String getUnauthorized() {
        return unauthorized;
    }

    public void setUnauthorized(String unauthorized) {
        ApplicationConfig.unauthorized = unauthorized;
    }

    public static boolean isSupportAccount(Long id) {
        return !myAccount.getId().equals(id);
    }

    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/");
        internalResourceViewResolver.setRedirectContextRelative(true);
        return internalResourceViewResolver;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/")
                .setCachePeriod(0);
        registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/css/")
                .setCachePeriod(0);
        registry.addResourceHandler("/images/**").addResourceLocations("/WEB-INF/images/")
                .setCachePeriod(0);
    }

    public boolean isConfirmViaMail() {
        return confirmViaMail;
    }

    public void setConfirmViaMail(boolean confirmViaMail) {
        ApplicationConfig.confirmViaMail = confirmViaMail;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}

