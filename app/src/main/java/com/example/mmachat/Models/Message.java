package com.example.mmachat.Models;

public class Message {
    private String userId, message, messageId;
    private Long timestampp;



    public Message(String userId, String messageId, Long timestampp) {
        this.userId = userId;
        this.messageId = messageId;
        this.timestampp = timestampp;
    }

    public Message(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public Message(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getTimestampp() {
        return timestampp;
    }

    public void setTimestampp(Long timestampp) {
        this.timestampp = timestampp;
    }
}
