package com.example.paulkiminventoryapp.model;

import androidx.annotation.NonNull;

public class Items {

    private long mId;
    private String mText;
    private long mCategoryId;
    private long mUpdateTime;

    // Default constructor
    public Items() {
        mUpdateTime = System.currentTimeMillis();
    }

    public Items(@NonNull String text) {
        mText = text;
        mUpdateTime = System.currentTimeMillis();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }
    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long categoryId) {
        mCategoryId = categoryId;
    }

    public long getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        mUpdateTime = updateTime;
    }

}