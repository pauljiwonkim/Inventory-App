package com.example.paulkiminventoryapp.model;

import androidx.annotation.NonNull;

public class Categories {

    private String mCategoryId;
    private String mText;
    private long mUpdateTime;


    public Categories(@NonNull String text) {
        mText = text;
        mUpdateTime = System.currentTimeMillis();
    }

    public void setId(String id) {
        mCategoryId = id;
    }

    public String getId() {
        return mCategoryId;
    }

    public String getText() {
        return mText;
    }

    public long getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        mUpdateTime = updateTime;

    }
}