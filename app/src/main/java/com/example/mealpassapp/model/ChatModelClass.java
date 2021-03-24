package com.example.mealpassapp.model;

public class ChatModelClass {
    private String id;
    private String senderId;
    private String senderName;
    private String senderRole;
    private String senderPic;
    private String recieverId;
    private String picUrl;
    private String message;
    private String date;
    private String time;
    private String fileName;
    private String fileType;
    private String flag;

    public ChatModelClass(){}

    public ChatModelClass(String id, String senderId, String senderName, String senderBusiness, String senderPic,
                          String recieverId, String picUrl, String message, String date, String time, String fileName, String fileType, String flag) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderBusiness;
        this.senderPic = senderPic;
        this.recieverId = recieverId;
        this.picUrl = picUrl;
        this.message = message;
        this.date = date;
        this.time = time;
        this.fileName = fileName;
        this.fileType = fileType;
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public String getSenderPic() {
        return senderPic;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFlag() {
        return flag;
    }
}
