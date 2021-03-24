package com.example.mealpassapp.model;

public class PayModelClass {
    private String id;
    private String imageUri;
    private String dealerId;
    private String fileType;
    private String date;

    public PayModelClass(){}

    public PayModelClass(String id, String imageUri, String dealerId, String fileType, String date) {
        this.id = id;
        this.imageUri = imageUri;
        this.dealerId = dealerId;
        this.fileType = fileType;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getImageUri() { return imageUri; }

    public String getDealerId() {
        return dealerId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getDate() {
        return date;
    }
}
