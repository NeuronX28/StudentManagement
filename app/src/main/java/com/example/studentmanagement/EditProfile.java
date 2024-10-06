package com.example.studentmanagement;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfile extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPhone, editTextQualification, editTextDesignation;
    private Button saveButton, cancelButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextQualification = findViewById(R.id.editTextQualification);
        editTextDesignation = findViewById(R.id.editTextDesignation);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        db = FirebaseFirestore.getInstance();

        // Load current user data to populate the EditTexts
        loadCurrentUserData();

        // Set save button click listener
        saveButton.setOnClickListener(v -> saveProfile());
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfile.this, "Editing Cancelled!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(EditProfile.this, ProfileActivity.class);
                startActivity(i);

            }
        });
    }

    private void loadCurrentUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    editTextName.setText(task.getResult().getString("name"));
                    editTextEmail.setText(task.getResult().getString("email"));
                    editTextPhone.setText(task.getResult().getString("phoneno"));
                    editTextQualification.setText(task.getResult().getString("qualification"));
                    editTextDesignation.setText(task.getResult().getString("designation"));

                }
            });
        }
    }

    private void saveProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String qualification = editTextQualification.getText().toString().trim();
            String designation = editTextDesignation.getText().toString().trim();

            // Update Firestore document
            db.collection("users").document(userId).update("name", name, "email", email, "phoneno", phone, "qualification", qualification, "designation", designation)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfile.this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
