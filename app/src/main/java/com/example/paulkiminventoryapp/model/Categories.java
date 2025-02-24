package com.example.paulkiminventoryapp.model;

import androidx.annotation.NonNull;

public class Categories {

    private String mCategoryId;
    private String mText;

    public Categories(@NonNull String text) {
        mText = text;
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

    }
