package com.example.paulkiminventoryapp.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.paulkiminventoryapp.model.User;

import java.util.ArrayList;

public class LoginDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "login.db";
    private static final int VERSION = 1;

    public LoginDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class UserDataTable {
        private static final String TAG = "LoginDatabase";
        private static final String TABLE = "Login";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";

        private static final String COL_EMAIL = "email";

        // private static final String COL_DATE_CREATED = "date_created";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + UserDataTable.TABLE + " (" +
                UserDataTable.COL_ID + " integer primary key autoincrement, " +
                UserDataTable.COL_USERNAME + " text, " +
                UserDataTable.COL_PASSWORD + " text, " +
                UserDataTable.COL_EMAIL + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("drop table if exists " + UserDataTable.TABLE);
        onCreate(db);
    }

    public long addUserData(String userName, String userPassword, String userEmail) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(UserDataTable.COL_USERNAME, userName);
        values.put(UserDataTable.COL_PASSWORD, userPassword);
        values.put(UserDataTable.COL_EMAIL, userEmail);
        long userDataId = db.insert(UserDataTable.TABLE, null, values);
        return userDataId;
    }

    public Cursor readAllData() {
        String query = "SELECT * FROM " + UserDataTable.TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public boolean updateUserData(String userName, String userPassword, String userEmail) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDataTable.COL_USERNAME, userName);
        values.put(UserDataTable.COL_PASSWORD, userPassword); // Update password if needed
        values.put(UserDataTable.COL_EMAIL, userEmail); // Update email if needed

        // Specify which row to update based on userName
        int rowsUpdated = db.update(
                UserDataTable.TABLE,
                values,
                UserDataTable.COL_USERNAME + " = ?",
                new String[]{userName}
        );

        // Return true if at least one row was updated, false otherwise
        return rowsUpdated > 0;
    }

    // Method to retrieve user details based on username
    public User getUserByUsername(String username) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                UserDataTable.COL_USERNAME,
                UserDataTable.COL_PASSWORD,
                UserDataTable.COL_EMAIL
        };
        String selection = UserDataTable.COL_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                UserDataTable.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            String password = cursor.getString(cursor.getColumnIndexOrThrow(UserDataTable.COL_PASSWORD));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(UserDataTable.COL_EMAIL));
            user = new User(username, password, email); // Create User object with retrieved data
            cursor.close();
        }

        return user;
    }

    //query database
    public ArrayList<String> getAllUsers() {
        ArrayList<String> userList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                UserDataTable.COL_USERNAME,
                UserDataTable.COL_PASSWORD,
                UserDataTable.COL_EMAIL,};

        Cursor cursor = db.query(
                UserDataTable.TABLE,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow(UserDataTable.COL_USERNAME));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(UserDataTable.COL_PASSWORD));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(UserDataTable.COL_EMAIL));
            userList.add("Username: " + username + ", Password: " + password + ", Email: " + email);
        }
        cursor.close();
        return userList;
    }

    public boolean validateCredentials(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String[] col = {UserDataTable.COL_ID};
        String selection = UserDataTable.COL_USERNAME + " = ? AND " + UserDataTable.COL_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(
                UserDataTable.TABLE, col, selection, selectionArgs,null, null,null);

        boolean isValid = (cursor.getCount() > 0);
        cursor.close();
        return isValid;

    }
}

