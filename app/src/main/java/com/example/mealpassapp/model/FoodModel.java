package com.example.mealpassapp.model;

public class FoodModel {
    private String id;
    private String foodTitle;
    private int price;
    private int points;
    private float discount;
    private String category;
    private String foodPic;
    private String sellerId;
    private String sellName;
    private String sellerPic;
    private String sellerToken;
    private String date;
    private boolean delivery;
    private String flag;

    public FoodModel() { }

    public FoodModel(String id, String foodTitle, int price, int points, float discount, String category, String foodPic,
                     String sellerId, String sellName, String sellerPic, String sellerToken, String date, boolean delivery, String flag) {
        this.id = id;
        this.foodTitle = foodTitle;
        this.price = price;
        this.points = points;
        this.discount = discount;
        this.category = category;
        this.foodPic = foodPic;
        this.sellerId = sellerId;
        this.sellName = sellName;
        this.sellerPic = sellerPic;
        this.sellerToken = sellerToken;
        this.date = date;
        this.delivery = delivery;
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public String getFoodTitle() {
        return foodTitle;
    }

    public int getPrice() {
        return price;
    }

    public int getPoints() {
        return points;
    }

    public float getDiscount() {
        return discount;
    }

    public String getCategory() {
        return category;
    }

    public String getFoodPic() {
        return foodPic;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getSellName() {
        return sellName;
    }

    public String getSellerPic() {
        return sellerPic;
    }

    public String getSellerToken() {
        return sellerToken;
    }

    public String getDate() {
        return date;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public String getFlag() {
        return flag;
    }
}
