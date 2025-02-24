package com.example.paulkiminventoryapp.model;

import androidx.annotation.NonNull;

public class Items {

    private String mId;
    private String mItemName;
    private String mItemCategoryId;
    private String mItemDesc;
    private long mitemQuantity;
    private double mitemPrice;
    private String mItemCategoryIdText;

    // Default constructor
    public Items(String id, String mItemName, String mItemDesc, long mitemQuantity, double mitemPrice) {
        this.mId = id;
        this.mItemName = mItemName;
        this.mItemDesc = mItemDesc;
        this.mitemQuantity = mitemQuantity;
        this.mitemPrice = mitemPrice;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getItemName() { return mItemName;}
    public void setItemName(String itemName) { mItemName = itemName;}
    public String getItemDesc() { return mItemDesc;}
    public void setItemDesc(String ItemDesc) { mItemDesc = ItemDesc;}

    public long getItemQuantity() { return mitemQuantity;}
    public void setItemQuantity(long itemQuantity) { mitemQuantity = itemQuantity;}

    public double getItemPrice() { return mitemPrice;}
    public void setItemPrice(double itemPrice) { mitemPrice = itemPrice;}


    public String getCategoryId() {
        return mItemCategoryId;
    }

    public void setCategoryId(String itemCategoryId) {
        mItemCategoryId = itemCategoryId;
    }

}