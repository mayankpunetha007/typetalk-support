package com.nulab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by mayan on 11/20/2016.
 */
@ConfigurationProperties
public class ApplicationConfig {

    public static String invalidTopicSubject;

    public static String invalidEmail;

    public static String gmailId;

    public static String gmailPassword;

    public static String typeTalkClientId;

    public static String typeTalkClientSecret;

    public static String baseUrl;

}

