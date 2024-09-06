package com.example.studentmanagement.auth.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagement.R;

public class LoginPage extends AppCompatActivity {

    private EditText email, password;
    private Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        TextView forgetPass = findViewById(R.id.forgot);
        TextView signUp = findViewById(R.id.signup);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        forgetPass.setOnClickListener(v -> {
            startActivity(new Intent(LoginPage.this, forgetpass.class));
        });

        signUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginPage.this, SignUp.class));
        });

        login.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            if (emailText.isEmpty() || passwordText.isEmpty()){
                email.setError("Email is required");
                password.setError("Password is required");
                return;
            }
            authenticateUser(emailText, passwordText);
        });
    }
    private void authenticateUser(String email, String password) {
        // Add your code here to authenticate the user
    }
}