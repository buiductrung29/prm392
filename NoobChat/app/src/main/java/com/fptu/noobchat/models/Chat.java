package com.fptu.noobchat.models;

public class Chat {
    private String sender;
    private String message;
    private String receiver;
    private boolean isseen;

    public Chat() {}

    public Chat(String sender, String message, String receiver, boolean isSeen) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.isseen = isSeen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public boolean getisseen() {
        return isseen;
    }

    public void setisseen(boolean isseen) {
        this.isseen = isseen;
    }
}
