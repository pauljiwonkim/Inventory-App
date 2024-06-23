package com.example.paulkiminventoryapp.model;

import androidx.annotation.NonNull;

public class Items {

    private String mId;
    private String mItemName;
    private String mitemCategory;
    private String mitemDesc;
    private long mitemQuantity;
    private double mitemPrice;
    private long mUpdateTime;

    public Items() {
        mUpdateTime = System.currentTimeMillis();
    }

    // Default constructor
    public Items(String id, String itemName, String mitemDesc, long mitemQuantity, double mitemPrice) {
        this.mId = id;
        this.mItemName = itemName;
        this.mitemDesc = mitemDesc;
        this.mitemQuantity = mitemQuantity;
        this.mitemPrice = mitemPrice;
        mUpdateTime = System.currentTimeMillis();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getItemName() { return mItemName;}
    public void setItemName(String itemName) { mItemName = itemName;}
    public String getItemDesc() { return mItemName;}
    public void setItemDesc(String itemDesc) { mitemDesc = itemDesc;}

    public long getItemQuantity() { return mitemQuantity;}
    public void setItemQuantity(long itemQuantity) { mitemQuantity = itemQuantity;}

    public double getItemPrice() { return mitemPrice;}
    public void setItemPrice(double itemPrice) { mitemPrice = itemPrice;}


    public String getCategoryId() {
        return mitemCategory;
    }

    public void setCategoryId(String itemCategory) {
        mitemCategory = itemCategory;
    }

    public long getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        mUpdateTime = updateTime;
    }

}