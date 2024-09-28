package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class upload_Activity extends AppCompatActivity {

    EditText uploadroll, uploadname, uploadclass, uploadprogram;
    Button saveButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();  // Initialize Firestore
        setContentView(R.layout.activity_upload_student);  // Set the layout

        // Initialize views
        uploadprogram = findViewById(R.id.uploadprogram);
        uploadroll = findViewById(R.id.uploadroll);
        uploadname = findViewById(R.id.uploadname);
        uploadclass = findViewById(R.id.uploadclass);
        saveButton = findViewById(R.id.saveButton);

        // Set up click listener for save button
        saveButton.setOnClickListener(v -> saveClassData());
    }

    private void saveClassData() {
        // Get the data from the EditText fields
        String roll = uploadroll.getText().toString().trim();
        String name = uploadname.getText().toString().trim();
        String className = uploadclass.getText().toString().trim();
        String program = uploadprogram.getText().toString().trim();

        // Check if any field is empty
        if (roll.isEmpty() || name.isEmpty() || className.isEmpty() || program.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map to store the data in Firestore
        Map<String, Object> studentData = new HashMap<>();
        studentData.put("roll", roll);
        studentData.put("name", name);
        studentData.put("class", className);
        studentData.put("program", program);

        // Add the data to Firestore
        db.collection("students")
                .add(studentData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Student data added successfully!", Toast.LENGTH_SHORT).show();
                    // After successfully saving the data, navigate to the next activity
                    Intent intent = new Intent(this, showActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding student data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
