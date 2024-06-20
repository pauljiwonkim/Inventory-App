package com.example.paulkiminventoryapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class LoginActivity extends AppCompatActivity {

    EditText userNameTxt;
    TextInputLayout passwordTextInputLayout;
    EditText passwordTxtField;
    Button loginButton;
    Button signUpButton;
    TextView tempTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameTxt = findViewById(R.id.userNameTxt);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        passwordTxtField = findViewById(R.id.passwordTxtField);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        tempTxt = findViewById(R.id.tempTxt);

        loginButton.setEnabled(false); // Initially disable loginButton

        // Touch listener to the root layout. Mainly used to close keyboard when user touches anywhere on screen
        View rootView = findViewById(android.R.id.content);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // TextWatcher to see if Password is filled
        passwordTxtField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Clear text when userNameTxt is clicked
        userNameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameTxt.setText("");
            }
        });

        // Temporary for testing
        // Displays text when button successfully pressed
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempButtonPress(1);
                Intent intent = new Intent(LoginActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
        });

        // Displays text when button successfully pressed
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempButtonPress(2);
            }
        });

    }

    // Update the state of the login button based on if both username and password text fields are filled
    private void updateLoginButtonState() {
        boolean isUsernameFilled = !userNameTxt.getText().toString().isEmpty();
        boolean isPasswordFilled = !passwordTxtField.getText().toString().isEmpty();
        loginButton.setEnabled(isUsernameFilled && isPasswordFilled);
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

    // Temporary function for buttons
    public void tempButtonPress(int a) {
        if (a == 1) {
            tempTxt.setText("Logged in Successfully!");
        } else if (a == 2) {
            tempTxt.setText("Sign Up!");
        }
    }
}
