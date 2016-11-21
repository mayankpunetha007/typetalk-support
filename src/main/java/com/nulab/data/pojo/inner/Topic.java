package com.nulab.data.pojo.inner;

import java.util.Date;

/**
 * Created by mayan on 11/20/2016.
 */
public class Topic {

    public Topic() {
    }

    public Topic(Long id, String name, String suggestion, boolean isDirectMessage, Date lastPostedAt, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.suggestion = suggestion;
        this.isDirectMessage = isDirectMessage;
        this.lastPostedAt = lastPostedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private Long id;

    private String name;

    private String suggestion;

    private boolean isDirectMessage;

    private Date lastPostedAt;

    private Date createdAt;

    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public boolean isIsDirectMessage() {
        return isDirectMessage;
    }

    public void setIsIsDirectMessage(boolean directMessage) {
        this.isDirectMessage = directMessage;
    }

    public Date getLastPostedAt() {
        return lastPostedAt;
    }

    public void setLastPostedAt(Date lastPostedAt) {
        this.lastPostedAt = lastPostedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
