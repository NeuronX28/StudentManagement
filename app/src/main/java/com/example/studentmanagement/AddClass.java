package com.example.studentmanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddClass extends AppCompatActivity {

    private EditText classNameEditText, courseNameEditText, sectionEditText, startDateEditText, endDateEditText;
    private Button saveButton, cancelButton;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

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

        // Handle Save button click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClassData();
            }
        });

        // Handle Cancel button click
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear all fields or close the activity
                clearFields();
            }
        });
    }

    // Function to save class data to Firebase Firestore
    private void saveClassData() {
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

        // Create a map to store the data
        Map<String, Object> classData = new HashMap<>();
        classData.put("className", className);
        classData.put("courseName", courseName);
        classData.put("section", section);
        classData.put("startDate", startDate);
        classData.put("endDate", endDate);

        // Add data to Firestore
        db.collection("classes")
                .add(classData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddClass.this, "Class added successfully!", Toast.LENGTH_SHORT).show();
                    clearFields(); // Clear the form after saving
                })
                .addOnFailureListener(e -> Toast.makeText(AddClass.this, "Error adding class: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Clear all input fields
    private void clearFields() {
        classNameEditText.setText("");
        courseNameEditText.setText("");
        sectionEditText.setText("");
        startDateEditText.setText("");
        endDateEditText.setText("");
    }
}
