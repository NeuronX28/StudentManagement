package com.example.studentmanagement;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewProfile;
    private TextView textViewName, textViewDesignation, textViewEmail, textViewPhone, textViewQualification;
    private Button buttonUpload, edit;

    private Uri imageUri;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageViewProfile = findViewById(R.id.imageView4);
        textViewName = findViewById(R.id.textView3); // Assuming this is the faculty name
        textViewDesignation = findViewById(R.id.desigFac); // Assuming this is the faculty designation
        textViewEmail = findViewById(R.id.emailId); // Assuming this is the email
        textViewPhone = findViewById(R.id.phoneNum); // Assuming this is the phone number
        textViewQualification = findViewById(R.id.qualification); // Assuming this is the qualification
        buttonUpload = findViewById(R.id.uploadButton);
        edit = findViewById(R.id.button3);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Load user data from Firestore
        loadUserData();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this,EditProfile.class);
                startActivity(i);
            }
        });

        // Set up click listener for upload button
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void loadUserData() {
        String userId = auth.getCurrentUser().getUid();
        DocumentReference docRef = firestore.collection("users").document(userId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                textViewName.setText(documentSnapshot.getString("name"));
                textViewDesignation.setText(documentSnapshot.getString("designation"));
                textViewEmail.setText(documentSnapshot.getString("email"));
                textViewPhone.setText(documentSnapshot.getString("phoneno"));
                textViewQualification.setText(documentSnapshot.getString("qualification"));

                // Load profile image from Firebase Storage
                String imageUrl = documentSnapshot.getString("profileImageUrl");
                if (imageUrl != null) {
                    Glide.with(ProfileActivity.this)
                            .load(imageUrl)
                            .into(imageViewProfile);
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference fileReference = storage.getReference("profileImages/" + auth.getCurrentUser().getUid());
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update the Firestore user document
                            String userId = auth.getCurrentUser().getUid();
                            firestore.collection("users").document(userId)
                                    .update("profileImageUrl", uri.toString())
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                        Glide.with(ProfileActivity.this).load(uri).into(imageViewProfile);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show());
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
