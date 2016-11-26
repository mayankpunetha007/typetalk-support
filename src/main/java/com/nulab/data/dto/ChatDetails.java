package com.nulab.data.dto;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Contains the conversation between customer and support representatives organised into topics
 */
@Component
@Entity
public class ChatDetails implements Serializable {

    @ManyToOne
    private
    SupportTicket supportTicket;
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private boolean support;
    @Column(nullable = false)
    private String chatContent;
    @Column(nullable = false)
    private Date time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSupport() {
        return support;
    }

    public void setSupport(boolean support) {
        this.support = support;
    }

    public String getChatContent() {
        return chatContent;
    }

    public void setChatContent(String chatContent) {
        this.chatContent = chatContent;
    }

    public SupportTicket getSupportTicket() {
        return supportTicket;
    }

    public void setSupportTicket(SupportTicket supportTicket) {
        this.supportTicket = supportTicket;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
