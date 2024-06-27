package com.example.paulkiminventoryapp.model;

public class User {

    private String mId;
    private String mUserName;
    private String mUserPassword;

    private String mUserEmail;


    // Default constructor
    public User(String mUserName, String mUserPassword, String mUserEmail) {
        this.mUserName = mUserName;
        this.mUserPassword = mUserPassword;
        this.mUserEmail = mUserEmail;
        mUpdateTime = System.currentTimeMillis();
    }

    public String getId() { return mId;}
    public void setId(String id) { mId = id;}

    public String getUserName() {return mUserName;}
    public void setUserName(String username) { mUserName = username;}

    public String getUserPassword() {return mUserPassword;}
    public void setUserPassword(String password) { mUserPassword = password;}

    public String getUserEmail() {return mUserEmail;}
    public void setUserEmail(String email) { mUserEmail = email;}

}