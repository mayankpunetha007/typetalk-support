package com.nulab.data.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by mayan on 11/19/2016.
 */
@Entity
public class ChatDetails implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private boolean support;

    @Column(nullable = false)
    private String chatContent;

    @ManyToOne
    SupportTicket supportTicket;

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
