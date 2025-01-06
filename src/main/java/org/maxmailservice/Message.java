package org.maxmailservice;

import java.time.LocalDateTime;

public class Message {
    private static int idCounter = 0;
    private int id;
    private String sender;
    private String recipient;
    private String content;
    private LocalDateTime timestamp;

    public Message(String sender, String recipient, String content) {
        this.id = idCounter++;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public Message(int id, String sender, String recipient, String content) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return id + "," + sender + "," + recipient + "," + content;
    }

    public String getFormattedMessage() {
        return "From: " + sender + "\nTo: " + recipient + "\nDate: " + timestamp + "\n\n" + content;
    }
}