package com.example.jil.myapplication.Model;

public class Chat {



    private String sender;
    private String receiver;
    private String message;
    private boolean isseen;
    private String type;
    private boolean isDownloaded;




    public Chat(String sender, String receiver, String message,boolean isseen,String type,boolean isDownloaded)
    {
        this.sender=sender;
        this.receiver=receiver;
        this.message=message;
        this.isseen=isseen;
        this.type=type;
        this.isDownloaded=isDownloaded;


    }

    public Chat()
    {

    }
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }
}
