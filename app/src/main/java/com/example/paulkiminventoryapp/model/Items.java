package com.example.paulkiminventoryapp.model;

import androidx.annotation.NonNull;

public class Items {

    private long mId;
    private String mitemName;
    private long mitemCategory;
    private String mitemDesc;
    private long mitemQuantity;
    private double mitemPrice;
    private long mUpdateTime;

    public Items() {
        mUpdateTime = System.currentTimeMillis();
    }

    // Default constructor
    public Items(long mitemId, String mitemName, String mitemDesc, long mitemQuantity, double mitemPrice) {
        this.mId = mitemId;
        this.mitemName = mitemName;
        this.mitemDesc = mitemDesc;
        this.mitemQuantity = mitemQuantity;
        this.mitemPrice = mitemPrice;
        mUpdateTime = System.currentTimeMillis();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getItemName() { return mitemName;}
    public void setItemName(String itemName) { mitemName = itemName;}
    public String getItemDesc() { return mitemName;}
    public void setItemDesc(String itemDesc) { mitemDesc = itemDesc;}

    public long getItemQuantity() { return mitemQuantity;}
    public void setItemQuantity(long itemQuantity) { mitemQuantity = itemQuantity;}

    public double getItemPrice() { return mitemPrice;}
    public void setItemPrice(double itemPrice) { mitemPrice = itemPrice;}


    public long getCategoryId() {
        return mitemCategory;
    }

    public void setCategoryId(long itemCategory) {
        mitemCategory = itemCategory;
    }

    public long getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        mUpdateTime = updateTime;
    }

}