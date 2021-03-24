package com.example.mealpassapp.model;

public class UsersModel {
    private String id;
    private String imageUri;
    private String name;
    private String account;
    private String phone="";
    private String password;
    private String type="";
    private double latitude;
    private double longtitude;
    private String token;
    private int flag;

    public UsersModel() { }

    public UsersModel(String id, String imgUri, String name, String account, String phone,
                      String password, String t, double latitude, double longtitude, String token, int flag) {
        this.id = id;
        this.imageUri = imgUri;
        this.name = name;
        this.account = account;
        this.phone = phone;
        this.password = password;
        this.type = t;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.token = token;
        this.flag = flag;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public String  getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAccount() {
        return account;
    }

    public String  getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getType() { return type; }

    public double getLatitude() {
        return latitude;
    }

    public String getImageUri() { return imageUri; }

    public String getToken() {
        return token;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getFlag() {
        return flag;
    }
}
