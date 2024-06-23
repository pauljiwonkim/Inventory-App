package com.example.paulkiminventoryapp.model;

public class User {

    private long mId;
    private String mUserName;
    private String mUserPassword;

    private String mUserEmail;

    private long mUpdateTime;

    // Default constructor
    public User() {
        mUpdateTime = System.currentTimeMillis();
    }

    public User(String mUserName, String mUserPassword, String mUserEmail) {
        this.mUserName = mUserName;
        this.mUserPassword = mUserPassword;
        this.mUserEmail = mUserEmail;
        mUpdateTime = System.currentTimeMillis();
    }

    public long getId() {return mId;}
    public void setId(long id) { mId = id;}

    public String getUserName() {return mUserName;}
    public void setUserName(String username) { mUserName = username;}

    public String getUserPassword() {return mUserPassword;}
    public void setUserPassword(String password) { mUserPassword = password;}

    public String getUserEmail() {return mUserEmail;}
    public void setUserEmail(String email) { mUserEmail = email;}

    // TODO
//    public long getCategoryId() {
//        return mCategoryId;
//    }
//
//    public void setCategoryId(long categoryId) {
//        mCategoryId = categoryId;
//    }

    public long getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        mUpdateTime = updateTime;
    }

}