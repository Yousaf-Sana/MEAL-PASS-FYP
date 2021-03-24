package com.example.mealpassapp.model;

public class ItemCommentsModelClass {
    private String id;
    private String itemId="";
    private String comment;
    private int rating;
    private String customerId;
    private String commenterName;
    private String customerPic;
    private String orderId;

    public ItemCommentsModelClass(){}

    public ItemCommentsModelClass(String id, String itemId, String comment, int rating, String customerId, String commenterName , String customerPic, String orderId) {
        this.id = id;
        this.itemId = itemId;
        this.comment = comment;
        this.rating = rating;
        this.customerId = customerId;
        this.commenterName = commenterName;
        this.customerPic = customerPic;
        this.orderId = orderId;

    }

    public String getId() {
        return id;
    }

    public String getItemId() {
        return itemId;
    }

    public String getComment() {
        return comment;
    }

    public int getRating() {
        return rating;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public String getCustomerPic() {
        return customerPic;
    }

    public String getOrderId() {
        return orderId;
    }
}
