package com.koti.dailymanager;

import com.google.firebase.database.Exclude;



public class DashboardItem {
    private String title;
    private String  imageUrl;
    private String key;

    public DashboardItem(){

    }

    public DashboardItem(String title, String image) {
        this.title = title;
        this.imageUrl = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String image) {
        this.imageUrl = image;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}

