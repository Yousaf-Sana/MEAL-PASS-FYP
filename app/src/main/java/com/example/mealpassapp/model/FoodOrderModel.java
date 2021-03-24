package com.example.mealpassapp.model;

public class FoodOrderModel {
    private String id;
    private String foodName;
    private String foodId;
    private String foodPic;
    private int points;
    private String sellerId;
    private String sellerName;
    private int foodPrice;
    private float discount;
    private int quantity;
    private String userId;
    private String userName;
    private String userPic;
    private String date;
    private String flag;
    private String userToken;
    private boolean isDeliver;

    public FoodOrderModel() { }

    public FoodOrderModel(String id, String foodName, String foodId, String foodPic, int points, String sellerId, String sellerName, int foodPrice, float discount,
                          int quantity, String userId, String userName, String userPic, String date, String flag, String token, boolean isDeliver) {
        this.id = id;
        this.foodName = foodName;
        this.foodId = foodId;
        this.foodPic = foodPic;
        this.points = points;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.foodPrice = foodPrice;
        this.discount = discount;
        this.quantity = quantity;
        this.userId = userId;
        this.userName = userName;
        this.userPic = userPic;
        this.date = date;
        this.flag = flag;
        this.userToken = token;
        this.isDeliver = isDeliver;
    }

    public String getId() {
        return id;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodId() {
        return foodId;
    }

    public String getFoodPic() {
        return foodPic;
    }

    public int getPoints() {
        return points;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public int getFoodPrice() {
        return foodPrice;
    }

    public float getDiscount() {
        return discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public String getDate() {
        return date;
    }

    public String getFlag() {
        return flag;
    }

    public String getUserToken() {
        return userToken;
    }

    public boolean isDeliver() {
        return isDeliver;
    }
}
