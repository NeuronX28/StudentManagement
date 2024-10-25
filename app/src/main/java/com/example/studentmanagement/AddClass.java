package com.example.studentmanagement;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AddClass extends AppCompatActivity {

    private EditText classNameEditText, courseNameEditText, sectionEditText, startDateEditText, endDateEditText;
    private Button saveButton, cancelButton;
    ImageView back;
    private FirebaseFirestore db;

    // Regular expression to validate date format dd/mm/yyyy
    private static final String DATE_PATTERN = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/[0-9]{4}$";

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

        // Back Button
        back = findViewById(R.id.go_back);

        // Set up the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        // Handle Save button click
        saveButton.setOnClickListener(v -> saveClassData());

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddClass.this, ClassList.class);
            startActivity(intent);
            clearFields();
        });

        back.setOnClickListener(view -> {
            Intent intent = new Intent(AddClass.this, ClassList.class);
            startActivity(intent);
        });

        // Date picker for start date
        startDateEditText.setOnClickListener(v -> showDatePickerDialog(startDateEditText));

        // Date picker for end date
        endDateEditText.setOnClickListener(v -> showDatePickerDialog(endDateEditText));

        bottomNavigationView.setSelectedItemId(R.id.navclasses);

        // Set listener for bottom navigation item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navsetting) {
                navToSettings();
                return true;
            } else if (id == R.id.navclasses) {
                navigateToClasses();
                return true;
            } else if (id == R.id.navprofile) {
                navigateToProfile();
                return true;
            }
            return false;
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

        // Validate section (single character)
        if (section.length() != 1) {
            Toast.makeText(this, "Section must be a single character.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate date formats
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            Toast.makeText(this, "Dates must be in the format dd/mm/yyyy.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if start date is before end date
        if (!isStartDateBeforeEndDate(startDate, endDate)) {
            Toast.makeText(this, "Start date must be before end date.", Toast.LENGTH_SHORT).show();
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

    // Validate date format using regex
    private boolean isValidDate(String date) {
        return Pattern.matches(DATE_PATTERN, date);
    }

    // Check if start date is before end date
    private boolean isStartDateBeforeEndDate(String startDate, String endDate) {
        String[] startParts = startDate.split("/");
        String[] endParts = endDate.split("/");

        // Extract day, month, and year
        int startDay = Integer.parseInt(startParts[0]);
        int startMonth = Integer.parseInt(startParts[1]);
        int startYear = Integer.parseInt(startParts[2]);

        int endDay = Integer.parseInt(endParts[0]);
        int endMonth = Integer.parseInt(endParts[1]);
        int endYear = Integer.parseInt(endParts[2]);

        // Compare years first
        if (startYear != endYear) {
            return startYear < endYear;
        }
        // Compare months if years are the same
        if (startMonth != endMonth) {
            return startMonth < endMonth;
        }
        // Finally compare days
        return startDay < endDay;
    }

    // Show DatePickerDialog
    private void showDatePickerDialog(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format day and month to two digits
                    String formattedDay = String.format("%02d", selectedDay);
                    String formattedMonth = String.format("%02d", selectedMonth + 1);
                    String date = formattedDay + "/" + formattedMonth + "/" + selectedYear; // dd/mm/yyyy
                    editText.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }

    // Clear all input fields
    private void clearFields() {
        classNameEditText.setText("");
        courseNameEditText.setText("");
        sectionEditText.setText("");
        startDateEditText.setText("");
        endDateEditText.setText("");
    }

    private void navToSettings() {
        Intent settingsIntent = new Intent(this, Settings.class);
        startActivity(settingsIntent);
        finish();
    }

    private void navigateToClasses() {
        Intent classesIntent = new Intent(this, ClassList.class);
        startActivity(classesIntent);
        finish();
    }

    private void navigateToProfile() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
        finish();
    }
}
