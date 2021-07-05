package com.koti.dailymanager;

import com.google.firebase.database.Exclude;

public class Upload {
    private String Name;
    private String quantity;
    private String note;
    private String expiryDate;
    private String imageUrl;
    private String key;
    private String Category;

    public Upload(String name, String quantity, String note, String expiryDate, String imageUrl) {
        Name = name;
        this.quantity = quantity;
        this.note = note;
        this.expiryDate = expiryDate;
        this.imageUrl = imageUrl;

    }

    public Upload(){

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    @Exclude
    public String getCategory() {
        return Category;
    }

    @Exclude
    public void setCategory(String category) {
        Category = category;
    }
}
