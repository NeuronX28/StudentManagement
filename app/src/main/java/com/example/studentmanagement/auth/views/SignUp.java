package com.example.studentmanagement.auth.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagement.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private EditText email, password, name,designation,qualification, phoneno, confirmpassword;
    private Button register;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find views by ID
        ImageView back = findViewById(R.id.go_back);
        email = findViewById(R.id.emailsign);
        password = findViewById(R.id.passsign);
        name = findViewById(R.id.namesign);
        phoneno = findViewById(R.id.phonesign);
        designation = findViewById(R.id.desigSign);
        qualification = findViewById(R.id.QualSign);
        confirmpassword = findViewById(R.id.passconfirmsign); // Make sure you have this field in your XML
        register = findViewById(R.id.register);

        // Set up back button listener
        back.setOnClickListener(v -> finish());

        // Set up register button listener
        register.setOnClickListener(v -> {
            String nameInput = name.getText().toString().trim();
            String desigInput = designation.getText().toString();
            String qualInput = qualification.getText().toString();
            String emailInput = email.getText().toString().trim();
            String phonenoInput = phoneno.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String confirmpasswordInput = confirmpassword.getText().toString().trim();

            dosignup(nameInput, desigInput, qualInput, emailInput, phonenoInput, passwordInput, confirmpasswordInput);
        });
    }

    private void dosignup(String name, String desigInput, String qualInput, String emailInput, String phonenoInput, String passwordInput, String confirmpasswordInput) {
        // Check if any field is empty
        if (name.isEmpty() || desigInput.isEmpty() || qualInput.isEmpty() || emailInput.isEmpty() || phonenoInput.isEmpty() || passwordInput.isEmpty() || confirmpasswordInput.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Invalid email format");
            email.requestFocus();
            return;
        }

        // Validate phone number (assuming basic 10 digit validation)
        if (phonenoInput.length() != 10) {
            phoneno.setError("Invalid phone number");
            phoneno.requestFocus();
            return;
        }

        // Check if passwords match
        if (!passwordInput.equals(confirmpasswordInput)) {
            confirmpassword.setError("Passwords do not match");
            confirmpassword.requestFocus();
            return;
        }

        // Password length validation
        if (passwordInput.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }

        // Proceed with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful, now store additional user info in Firestore
                        String userId = mAuth.getCurrentUser().getUid();

                        // Prepare user data
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("designation", desigInput);
                        user.put("qualification", qualInput);
                        user.put("email", emailInput);
                        user.put("phoneno", phonenoInput);

                        // Store user information in Firestore
                        db.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    // Display toast message on successful Firestore data storage
                                    Toast.makeText(SignUp.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    finish(); // Close activity after success
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignUp.this, "Failed to store user info: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        // Handle failure, e.g., network error, email already in use, etc.
                        Toast.makeText(SignUp.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
