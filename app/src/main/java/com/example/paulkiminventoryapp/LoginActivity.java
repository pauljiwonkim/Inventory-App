package com.example.paulkiminventoryapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.paulkiminventoryapp.repo.LoginDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.widget.Toast;

// LoginActivity
public class LoginActivity extends AppCompatActivity implements SignUpDialogFragment.OnUserEnteredListener {
    private final int REQUEST_SMS_CODE = 0; // Request code for SMS permission
    private final static int NOTIFICATION_ID = 0; // ID for notifications
    EditText userNameTxt; // Username input field
    EditText passwordTxtField; // Password input field
    Button loginButton; // Login button
    Button signUpButton; // Signup button
    LoginDatabase mLoginDatabase; // Instance of database handler
    boolean smsPermission = false; // SMS permission status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize notification channels. Throw an exception if fail
        try {
            loginNotificationChannel();
            signupNotificationChannel();
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        // Initialize
        mLoginDatabase = new LoginDatabase(this);
        userNameTxt = findViewById(R.id.userNameTxt);
        passwordTxtField = findViewById(R.id.passwordTxtField);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        Button databaseButton = findViewById(R.id.databaseButton);

        View rootView = findViewById(android.R.id.content);
        loginButton.setEnabled(false); // Initially disable login button

        // Check and request SMS permission if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    REQUEST_SMS_CODE);
        }

        // Set onTouchListener for root view to hide keyboard
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                }
                return false;
            }
        });

        // Add TextWatcher to username input field to enable login button when filled
        userNameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState(); // Update login button state
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Add TextWatcher to password input field to enable login button when filled
        passwordTxtField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState(); // Update login button state
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Set onClickListener for login button to validate credentials and login
        loginButton.setOnClickListener(v -> {
            String userEnteredUsername = userNameTxt.getText().toString();
            String userEnteredPassword = passwordTxtField.getText().toString();

            // Validate credentials and login user
            if (mLoginDatabase.validateCredentials(userEnteredUsername, userEnteredPassword)) {
                Toast.makeText(this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                if (hasSMSPermissions()) {
                    sendLoginNotification(); // Send login notification
                }
                // Start CategoryActivity with SMS permission status
                Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
                intent.putExtra("smsPermission", smsPermission);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            }
        });

        // Set onClickListener for sign-up button to show sign-up dialog
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });

        // Set onClickListener for database button to navigate to database view
        databaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDatabaseView();
            }
        });

    }

    // Navigate to database view activity. Demonstration Purposes
    public void navigateToDatabaseView() {
        Intent intent = new Intent(LoginActivity.this, UserListActivity.class);
        startActivity(intent);
    }

    // Update the login button state based on username and password input fields
    private void updateLoginButtonState() {
        boolean isUsernameFilled = !userNameTxt.getText().toString().isEmpty();
        boolean isPasswordFilled = !passwordTxtField.getText().toString().isEmpty();
        loginButton.setEnabled(isUsernameFilled && isPasswordFilled);
    }

    // Show sign-up dialog using FragmentManager
    private void showSignUpDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SignUpDialogFragment signUpDialogFragment = new SignUpDialogFragment();
        signUpDialogFragment.show(fragmentManager, "sign_up_dialog");
    }

    // Handle user input from sign-up dialog
    @Override
    public void onUserEntered(String userName, String userPassword, String userEmail) {
        // Check if all fields are filled
        if (!userName.isEmpty() && !userPassword.isEmpty() && !userEmail.isEmpty()) {
            try {
                long id = mLoginDatabase.addUserData(userName, userPassword, userEmail); // Add User to database
                if (id != -1) {
                    Toast.makeText(this, "Successfully signed up!", Toast.LENGTH_SHORT).show();
                    sendSignupNotification(); // Send sign-up notification
                } else {
                    Toast.makeText(this, "Error during sign up. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error thrown", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Check if SMS permissions are granted
    private boolean hasSMSPermissions() {
        String smsPermission = android.Manifest.permission.SEND_SMS;
        if (ContextCompat.checkSelfPermission(this, smsPermission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{smsPermission}, REQUEST_SMS_CODE);
            return false;
        } else {
            return true;
        }
    }

    // Handle the result of the SMS permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_CODE) {
            // Permission denied
            smsPermission = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED; // Permission granted
        }
    }

    // Set up the notification channel for login notifications
    private void loginNotificationChannel() {
        CharSequence name = getString(R.string.login_channel_name); // Channel name
        String description = getString(R.string.login_channel_description); // Channel description
        int importance = NotificationManager.IMPORTANCE_HIGH; // Notification importance level
        NotificationChannel channel = new NotificationChannel("1", name, importance);
        channel.setDescription(description);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(channel);
    }

    // Send a notification when user logs in
    private void sendLoginNotification() {
        try {
            // Build the notification
            Notification notification = new NotificationCompat.Builder(this, "1")
                    .setSmallIcon(R.drawable.notification_icon) // Icon for notification
                    .setContentTitle("Login Notification") // Notification title
                    .setContentText("You have successfully logged in.") // Notification content text
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // Notification priority
                    .build();
            // Get the NotificationManager and send the notification
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
        } catch (Exception e) {
            Toast.makeText(this, "Error thrown", Toast.LENGTH_SHORT).show(); // Show error if notification fails
        }
    }

    // Set up the notification channel for sign-up notifications
    private void signupNotificationChannel() {
        CharSequence name = getString(R.string.signup_channel_name); // Channel name
        String description = getString(R.string.signup_channel_description); // Channel description
        int importance = NotificationManager.IMPORTANCE_HIGH; // Notification importance level
        NotificationChannel channel = new NotificationChannel("2", name, importance);
        channel.setDescription(description);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(channel);
    }

    // Send a notification when user signs up
    private void sendSignupNotification() {
        try {
            // Build the notification
            Notification notification = new NotificationCompat.Builder(this, "2")
                    .setSmallIcon(R.drawable.notification_icon) // Icon for the notification
                    .setContentTitle("Signup Notification") // Notification title
                    .setContentText("You have successfully created an account.") // Notification content
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // Notification priority
                    .build();
            // Get the NotificationManager and send the notification
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
        } catch (Exception e) {
            Toast.makeText(this, "Error thrown", Toast.LENGTH_SHORT).show(); // Show error if notification fails
        }
    }


}
