package com.koti.dailymanager;

public class User {
    private String username,email,phoneNumber,profession;

    public User(){

    }

    public User(String username, String email,  String phoneNumber, String profession) {
        this.username = username;
        this.email = email;

        this.phoneNumber = phoneNumber;
        this.profession = profession;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }


}
