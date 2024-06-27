package com.example.paulkiminventoryapp;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.example.paulkiminventoryapp.repo.LoginDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;

// Activity for user User
public class LoginActivity extends AppCompatActivity implements SignUpDialogFragment.OnUserEnteredListener{
    private final int REQUEST_SMS_CODE = 0;
    EditText userNameTxt;
    EditText passwordTxtField;
    Button loginButton;
    Button signUpButton;
    TextView tempTxt;
    LoginDatabase mLoginDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginDatabase = new LoginDatabase(this);
        userNameTxt = findViewById(R.id.userNameTxt);
        passwordTxtField = findViewById(R.id.passwordTxtField);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        tempTxt = findViewById(R.id.tempTxt);
        Button databaseButton = findViewById(R.id.databaseButton);

        // Touch listener to the root layout. Mainly used to close keyboard when user touches anywhere on screen
        View rootView = findViewById(android.R.id.content);

        loginButton.setEnabled(false); // Initially disable loginButton

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                }
                return false;
            }
        });

        // TextWatcher to see if Username is filled
        userNameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // TextWatcher to see if Password is filled
        passwordTxtField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        databaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDatabaseView();
            }
        });

        // Clear text when userNameTxt is clicked
        userNameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameTxt.setText("");
            }
        });

        // Clear text when passwordTxt is clicked
        userNameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordTxtField.setText("");
            }
        });

        // Displays text when button successfully pressed
        loginButton.setOnClickListener(v -> {
            String userEnteredUsername = userNameTxt.getText().toString();
            String userEnteredPassword = passwordTxtField.getText().toString();

            if(mLoginDatabase.validateCredentials(userEnteredUsername, userEnteredPassword)){
                Toast.makeText(this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            }

        });

        // Displays text when button successfully pressed
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });
//        if (!hasPermissions()) {
//            requestSMSPermission();
//        }
    }

    // Creates an instance and displays it using FragmentManager
    private void showSignUpDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SignUpDialogFragment signUpDialogFragment = new SignUpDialogFragment();
        signUpDialogFragment.show(fragmentManager, "sign_up_dialog");
    }

    public void navigateToDatabaseView(){
        Intent intent = new Intent(LoginActivity.this, UserListActivity.class);
        startActivity(intent);
    }

    // Update the state of the login button based on if both username and password text fields are filled
    private void updateLoginButtonState() {
        boolean isUsernameFilled = !userNameTxt.getText().toString().isEmpty();
        boolean isPasswordFilled = !passwordTxtField.getText().toString().isEmpty();
        loginButton.setEnabled(isUsernameFilled && isPasswordFilled);
    }

    //Handles user input for sign up
    @Override
    public void onUserEntered(String userName, String userPassword, String userEmail) {
        // Check if both fields are filled
        if (!userName.isEmpty() && !userPassword.isEmpty() && !userEmail.isEmpty()) {
            long id = mLoginDatabase.addUserData(userName, userPassword, userEmail); // Add user to database
            if (id != -1) {
                Toast.makeText(this, "Successfully signed up!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error during sign up. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Hide the keyboard
    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }

    // TODO Finish Below
//    // Check if SMS permission is granted
//    private boolean hasPermissions() {
//        String smsPermission = Manifest.permission.SEND_SMS;
//        if (ContextCompat.checkSelfPermission(this, smsPermission)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, smsPermission )) {
//                showPermissionRationaleDialog(smsPermission);
//            } else {
//                ActivityCompat.requestPermissions(this,
//                        new String[] { smsPermission }, REQUEST_SMS_CODE);
//            }
//            return false;
//        }
//        return true;
//    }
//
//
//    // Show rationale dialog for SMS permission
//    private void showPermissionRationaleDialog(String permission) {
//        new AlertDialog.Builder(this)
//                .setTitle("SMS Permission Required")
//                .setMessage("Permission is required to send SMS notifications.")
//                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, REQUEST_SMS_CODE);
//                    }
//                })
//                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(LoginActivity.this, "SMS permission denied.", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .create()
//                .show();
//    }
}


