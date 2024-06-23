package com.example.paulkiminventoryapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paulkiminventoryapp.repo.LoginDatabase;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {
    RecyclerView loginRecycler;
    ArrayList<String> user_id, user_name, user_password, user_email;
    LoginDatabase mLoginDatabase;
    CustomLoginAdapter customLoginAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_database);

        loginRecycler = findViewById(R.id.login_recycler);
        mLoginDatabase = new LoginDatabase(this);

        // Initialize ArrayLists to store user data
        user_id = new ArrayList<>();
        user_name = new ArrayList<>();
        user_password = new ArrayList<>();
        user_email = new ArrayList<>();

        //Populate from database
        displayData();

        //Initialize adapter with populated list and set it to Recycler view
        customLoginAdapter = new CustomLoginAdapter(UserListActivity.this, user_id, user_name, user_password, user_email);
        loginRecycler.setAdapter(customLoginAdapter);
        loginRecycler.setLayoutManager(new LinearLayoutManager(UserListActivity.this));

    }

    void displayData(){
        Cursor cursor = mLoginDatabase.readAllData();
        if (cursor == null || cursor.getCount() == 0){
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()){
                user_id.add(cursor.getString(0));
                user_name.add(cursor.getString(1));
                user_password.add(cursor.getString(2));
                user_email.add(cursor.getString(3));
            }
        }

    }
}
