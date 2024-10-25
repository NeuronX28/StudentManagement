package com.example.studentmanagement.auth.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagement.R;
import com.google.firebase.auth.FirebaseAuth;

public class forgetpass extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpass);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        ImageView back = findViewById(R.id.go_back);
        emailEditText = findViewById(R.id.email_edit_text); // Add id to your XML for the email EditText
        resetPasswordButton = findViewById(R.id.reset_password_button); // Add id to your XML for the button

        back.setOnClickListener(v -> {
            finish();
        });

        resetPasswordButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(forgetpass.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                resetPassword(email);
            }
        });
    }

    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(forgetpass.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(forgetpass.this, "Unable to send reset mail", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
