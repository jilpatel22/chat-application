package com.example.jil.myapplication.Model;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String lastSeen;
    private String txt_status;
    private String phone_no;


    public User(String id, String username, String imageURL, String status, String lastSeen, String txt_status, String phone_no)
    {
        this.id=id;
        this.username=username;
        this.imageURL=imageURL;
        this.status=status;
        this.lastSeen=lastSeen;
        this.txt_status=txt_status;
        this.phone_no=phone_no;
    }

    public User(){}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getTxt_status() {
        return txt_status;
    }

    public void setTxt_status(String txt_status) {
        this.txt_status = txt_status;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }
}

