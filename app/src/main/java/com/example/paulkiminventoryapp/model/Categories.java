package com.example.paulkiminventoryapp.model;

import androidx.annotation.NonNull;

public class Categories {

    private long mCategoryId;
    private String mText;
    private long mUpdateTime;


    public Categories(@NonNull String text) {
        mText = text;
        mUpdateTime = System.currentTimeMillis();
    }

    public void setId(long id) {
        mCategoryId = id;
    }

    public long getId() {
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