package com.nulab.data.dto;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by mayan on 11/19/2016.
 */
@Entity
public class SupportInfo implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private SupportTicket supportTicket;

    @OneToMany
    private ChatDetails chatDetails;

    private String topicId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SupportTicket getSupportTicket() {
        return supportTicket;
    }

    public void setSupportTicket(SupportTicket supportTicket) {
        this.supportTicket = supportTicket;
    }

    public ChatDetails getChatDetails() {
        return chatDetails;
    }

    public void setChatDetails(ChatDetails chatDetails) {
        this.chatDetails = chatDetails;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
}
