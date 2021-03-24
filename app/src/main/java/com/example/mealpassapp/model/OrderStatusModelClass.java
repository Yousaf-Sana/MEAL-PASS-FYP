package com.example.mealpassapp.model;

public class OrderStatusModelClass {
    private String id;
    private String orderId="";
    private String custId="";
    private String status;
    private String itemId;
    private String item_name;
    private String itemPic;
    private String dealername;
    private String dealerId="";
    private float itemDiscount;
    private int quantity;
    private int price;
    private String dateTime;
    private String response="";

    public  OrderStatusModelClass(){}

    public OrderStatusModelClass(String id,String orderId, String custId, String status, String itemId, String item_name, String itemPic,
                                 String dealerId, String dealername, int quantity, int price, Float itemDiscount, String dateTime, String response) {
        this.id = id;
        this.orderId = orderId;
        this.custId = custId;
        this.status = status;
        this.itemId = itemId;
        this.item_name = item_name;
        this.itemPic = itemPic;
        this.dealerId = dealerId;
        this.dealername = dealername;
        this.price = price;
        this.quantity = quantity;
        this.itemDiscount = itemDiscount;
        this.dateTime = dateTime;
        this.response = response;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() { return orderId; }

    public String getCustId() {
        return custId;
    }

    public String getStatus() {
        return status;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getDealerId() { return dealerId;}

    public String getItemPic() {
        return itemPic;
    }

    public String getDealername() {
        return dealername;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getItemDiscount() {
        return itemDiscount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getResponse() { return response; }
}
