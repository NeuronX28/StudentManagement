package com.example.studentmanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class EditClass extends AppCompatActivity {

    private EditText classNameEditText, courseNameEditText, sectionEditText, startDateEditText, endDateEditText;
    private Button saveButton, cancelButton;
    private FirebaseFirestore db;
    private String classId; // Class ID to edit
    private ListenerRegistration registration; // For Firestore listener

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Bind EditText fields
        classNameEditText = findViewById(R.id.class_name);
        courseNameEditText = findViewById(R.id.course_name);
        sectionEditText = findViewById(R.id.section);
        startDateEditText = findViewById(R.id.start_date);
        endDateEditText = findViewById(R.id.end_date);

        // Bind Buttons
        saveButton = findViewById(R.id.button);
        cancelButton = findViewById(R.id.button2);

        // Get the class ID from the Intent
        classId = getIntent().getStringExtra("CLASS_ID");

        // Check if classId is null
        if (classId == null) {
            Toast.makeText(this, "Error: Class ID not found", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if class ID is not found
            return; // Exit the method early
        }

        // Fetch existing class data
        fetchClassData();

        // Handle Save button click
        saveButton.setOnClickListener(v -> updateClassData());

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> {
            clearFields(); // Clear all fields
            finish(); // Close the activity
        });
    }

    // Fetch existing class data from Firestore
    private void fetchClassData() {
        DocumentReference docRef = db.collection("classes").document(classId);
        registration = docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(EditClass.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String className = documentSnapshot.getString("className");
                String courseName = documentSnapshot.getString("courseName");
                String section = documentSnapshot.getString("section");
                String startDate = documentSnapshot.getString("startDate");
                String endDate = documentSnapshot.getString("endDate");

                // Set data to EditText fields
                classNameEditText.setText(className);
                courseNameEditText.setText(courseName);
                sectionEditText.setText(section);
                startDateEditText.setText(startDate);
                endDateEditText.setText(endDate);
            } else {
                Toast.makeText(EditClass.this, "Class not found!", Toast.LENGTH_SHORT).show();
                finish(); // Close activity if class not found
            }
        });
    }

    // Function to update class data in Firebase Firestore
    private void updateClassData() {
        String className = classNameEditText.getText().toString().trim();
        String courseName = courseNameEditText.getText().toString().trim();
        String section = sectionEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();

        // Validate input
        if (className.isEmpty() || courseName.isEmpty() || section.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map to store the updated data
        Map<String, Object> classData = new HashMap<>();
        classData.put("className", className);
        classData.put("courseName", courseName);
        classData.put("section", section);
        classData.put("startDate", startDate);
        classData.put("endDate", endDate);

        // Update data in Firestore
        db.collection("classes").document(classId)
                .set(classData) // Using set() to overwrite the document
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditClass.this, "Class updated successfully!", Toast.LENGTH_SHORT).show();
                    clearFields(); // Clear the form after saving
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> Toast.makeText(EditClass.this, "Error updating class: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Clear all input fields
    private void clearFields() {
        classNameEditText.setText("");
        courseNameEditText.setText("");
        sectionEditText.setText("");
        startDateEditText.setText("");
        endDateEditText.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove Firestore listener to prevent memory leaks
        if (registration != null) {
            registration.remove();
        }
    }
}
