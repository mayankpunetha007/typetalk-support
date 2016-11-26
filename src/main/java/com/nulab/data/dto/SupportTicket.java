package com.nulab.data.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Table to create a support ticket for the user
 */
@Entity
public class SupportTicket implements Serializable {

    @OneToMany
    List<ChatDetails> chatDetails;
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String accessKey;
    @Column(nullable = false)
    private String userMail;
    @Column(nullable = false)
    private String topic;
    @Column(unique = true)
    private Long topicId;

    public SupportTicket() {
    }

    public SupportTicket(String userName, String accessKey, String userMail, String topic) {
        this.userName = userName;
        this.accessKey = accessKey;
        this.userMail = userMail;
        this.topic = topic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<ChatDetails> getChatDetails() {
        return chatDetails;
    }

    public void setChatDetails(List<ChatDetails> chatDetails) {
        this.chatDetails = chatDetails;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
}
