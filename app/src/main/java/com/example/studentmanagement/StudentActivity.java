package com.example.studentmanagement;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentActivity extends AppCompatActivity implements StudentAdapter.OnStudentEditListener {

    private RecyclerView recyclerView;
    private List<Student> studentList;
    private StudentAdapter adapter;
    private SearchView searchView;
    private FirebaseFirestore db;
    ImageView backStud;

    //update declarations
    EditText uploadroll, uploadname, uploadclass, uploadprogram;
    Button saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        try {
            // Initialize views
            recyclerView = findViewById(R.id.recyclerView);
            searchView = findViewById(R.id.searchView);
            FloatingActionButton fab = findViewById(R.id.fab);
            backStud = findViewById(R.id.back_stud);

            backStud.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(StudentActivity.this,ClassList.class);
                    startActivity(i);
                }
            });


            // Set up the BottomNavigationView
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
            bottomNavigationView.setSelectedItemId(R.id.navclasses);

            // Set listener for bottom navigation item clicks
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.navsetting) {
                        // Toast.makeText(ClassList.this, "Settings selected", Toast.LENGTH_SHORT).show();
                        navToSettings();
                        return true;
                    } else if (id == R.id.navclasses) {
                        // Toast.makeText(ClassList.this, "Calendar selected", Toast.LENGTH_SHORT).show();
                        navToClasses();
                        return true;
                    } else if (id == R.id.navprofile) {
                        //Toast.makeText(ClassList.this, "Profile selected", Toast.LENGTH_SHORT).show();
                        navigateToProfile();
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            studentList = new ArrayList<>();
            adapter = new StudentAdapter(studentList, (StudentAdapter.OnStudentEditListener) this,this::deleteStudent);

            // LayoutManager
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            recyclerView.setAdapter(adapter);

            db = FirebaseFirestore.getInstance();  // Firestore initialization

            // Log Firestore initialization
            Log.d("StudentActivity", "Firestore initialized");

            // Retrieve className from Intent
            String className = getIntent().getStringExtra("className");
            if (className != null) {
                Log.d("StudentActivity", "Class name: " + className);
                loadStudentData(className);
            } else {
                Toast.makeText(this, "Class name is missing", Toast.LENGTH_SHORT).show();
            }

            // Floating action button to upload new students
            fab.setOnClickListener(view -> {
                //Intent intent = new Intent(StudentActivity.this, upload_Activity.class);
                //startActivity(intent);
                showAddStudentDialog();
            });

            // Search functionality
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchList(newText);
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e("StudentActivity", "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error occurred in onCreate", Toast.LENGTH_SHORT).show();
        }




    }

    private void loadStudentData(String className) {
        try {
            // Fetching data from Firestore
            db.collection("students")
                    .whereEqualTo("class", className)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            studentList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String roll = document.getString("roll");
                                String name = document.getString("name");
                                String program = document.getString("program");

                                Log.d("StudentActivity", "Loaded student: " + name);

                                // Adding student to list
                                studentList.add(new Student(roll, name, className, program));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("StudentActivity", "Task failed: " + task.getException());
                            Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("StudentActivity", "Firestore fetch failed: " + e.getMessage(), e);
                        Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e("StudentActivity", "Error in loadStudentData: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading student data", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchList(String text) {
        ArrayList<Student> searchList = new ArrayList<>();
        for (Student student : studentList) {
            // Check if the query matches the student's name or roll number (case-insensitive)
            if (student.getName().toLowerCase().contains(text.toLowerCase()) ||
                    student.getRoll().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(student);
            }
        }
        adapter.searchDataList(searchList);
    }


    @SuppressLint("MissingInflatedId")
    public void onEdit(Student student) {
        String studentRoll = student.getRoll();
        String currentClass = student.getClassName();

        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_student, null);

        // Create the dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Initialize EditText fields
        EditText editName = dialogView.findViewById(R.id.editname);
        EditText editRoll = dialogView.findViewById(R.id.editroll);
        EditText editClass = dialogView.findViewById(R.id.editclass);
        EditText editProgram = dialogView.findViewById(R.id.editprogram);
        Button updateButton = dialogView.findViewById(R.id.updateButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Set the current values in the EditText fields
        editName.setText(student.getName());
        editRoll.setText(student.getRoll());
        editClass.setText(student.getClassName());
        editProgram.setText(student.getProgram());

        // Create the AlertDialog instance
        androidx.appcompat.app.AlertDialog dialog = builder.create();

        // Query Firestore to find the document ID based on the roll number
        db.collection("students")
                .whereEqualTo("roll", studentRoll)
                .whereEqualTo("class", currentClass)  // Ensure it's within the same class
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Assuming roll number is unique and you get a single document
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String documentId = document.getId(); // Get the document ID

                        // Set up the Update button click listener
                        updateButton.setOnClickListener(v -> {
                            // Get the updated data from the EditText fields
                            String updatedName = editName.getText().toString().trim();
                            String updatedRoll = editRoll.getText().toString().trim();
                            String updatedClass = editClass.getText().toString().trim();
                            String updatedProgram = editProgram.getText().toString().trim();

                            // Validate that all fields are filled
                            if (updatedName.isEmpty() || updatedRoll.isEmpty() || updatedClass.isEmpty() || updatedProgram.isEmpty()) {
                                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // If the roll number or class has changed, check if the new roll number is unique
                            if (!studentRoll.equals(updatedRoll) || !currentClass.equals(updatedClass)) {
                                db.collection("students")
                                        .whereEqualTo("roll", updatedRoll)
                                        .whereEqualTo("class", updatedClass)
                                        .get()
                                        .addOnCompleteListener(checkTask -> {
                                            if (checkTask.isSuccessful() && !checkTask.getResult().isEmpty()) {
                                                // Roll number already exists in the updated class
                                                Toast.makeText(this, "Roll number already exists in the selected class. Please enter a different one.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Roll number is unique, proceed with the update
                                                updateStudentData(documentId, updatedName, updatedRoll, updatedClass, updatedProgram, dialog);
                                            }
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, "Error checking roll number: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            } else {
                                // If roll number and class remain the same, proceed with the update
                                updateStudentData(documentId, updatedName, updatedRoll, updatedClass, updatedProgram, dialog);
                            }
                        });

                        // Set up the Cancel button click listener
                        cancelButton.setOnClickListener(v -> dialog.dismiss());

                        // Show the dialog
                        dialog.show();
                    } else {
                        Toast.makeText(this, "No student found with the given roll number", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error retrieving student data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateStudentData(String documentId, String updatedName, String updatedRoll, String updatedClass, String updatedProgram, androidx.appcompat.app.AlertDialog dialog) {
        // Create a map to store the updated data
        Map<String, Object> updatedStudentData = new HashMap<>();
        updatedStudentData.put("name", updatedName);
        updatedStudentData.put("roll", updatedRoll);
        updatedStudentData.put("class", updatedClass);
        updatedStudentData.put("program", updatedProgram);

        // Update the student's data in Firebase using the document ID
        db.collection("students")
                .document(documentId)
                .update(updatedStudentData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Student data updated successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); // Dismiss the dialog after successful update
                    loadStudentData(updatedClass); // Reload data for the updated class
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating student data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void deleteStudent(Student student) {
        // Log the student roll to ensure it's correct
        String studentRoll = student.getRoll();
        Log.d("StudentActivity", "Deleting student with roll: " + studentRoll);

        // Query Firestore to find the document ID based on the roll number
        db.collection("students")
                .whereEqualTo("roll", studentRoll) // Assuming 'roll' is the field in Firestore
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Assuming roll number is unique and you get a single document
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String documentId = document.getId(); // Get the document ID

                        // Create an AlertDialog to confirm deletion
                        AlertDialog dialog = new AlertDialog.Builder(this) // Use androidx.appcompat.app.AlertDialog
                                .setTitle("Delete Student")
                                .setMessage("Are you sure you want to delete this student?")
                                .setPositiveButton("Yes", (dialogInterface, which) -> {
                                    db.collection("students").document(documentId).delete()
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("StudentActivity", "Student deleted successfully");
                                                Toast.makeText(this, "Student deleted successfully", Toast.LENGTH_SHORT).show();
                                                // Reload the student data after deletion
                                                String className = getIntent().getStringExtra("className");
                                                loadStudentData(className);
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("StudentActivity", "Error deleting student: " + e.getLocalizedMessage(), e);
                                                Toast.makeText(this, "Error deleting student: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .setNegativeButton("No", (dialogInterface, which) -> dialogInterface.dismiss())
                                .show(); // Show the confirmation dialog
                        

                    } else {
                        Toast.makeText(this, "No student found with the given roll number", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("StudentActivity", "Error retrieving student data: " + e.getMessage());
                    Toast.makeText(this, "Error retrieving student data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void navToSettings() {
        Intent settingsIntent = new Intent(this, Settings.class);
        startActivity(settingsIntent);
    }

    private void navToClasses() {
        Intent classesIntent = new Intent(this, ClassList.class);
        startActivity(classesIntent);
    }

    private void navigateToProfile() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    //pop up for adding student
    @SuppressLint("MissingInflatedId")
    private void showAddStudentDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_student, null);

        // Create the dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Initialize the views from the dialog layout
        uploadprogram = dialogView.findViewById(R.id.uploadprogram);
        uploadroll = dialogView.findViewById(R.id.uploadroll);
        uploadname = dialogView.findViewById(R.id.uploadname);
        uploadclass = dialogView.findViewById(R.id.uploadclass);
        saveButton = dialogView.findViewById(R.id.saveButton);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Create the AlertDialog instance
        androidx.appcompat.app.AlertDialog dialog = builder.create();

        // Set up the Save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClassData(dialog);
            }
        });

        // Set up the Cancel button click listener
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();  // Dismiss the dialog on cancel
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void saveClassData(androidx.appcompat.app.AlertDialog dialog) {
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
                .whereEqualTo("roll", roll)
                .whereEqualTo("class", className)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Roll number already exists
                        Toast.makeText(this, "Roll number already exists, please enter a different one", Toast.LENGTH_SHORT).show();
                    } else {
                        // Roll number is unique, add the student
                        Map<String, Object> student = new HashMap<>();
                        student.put("name", name);
                        student.put("roll", roll);
                        student.put("class", className);
                        student.put("program", program);

                        db.collection("students").add(student)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Student added successfully!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    loadStudentData(className); // Refresh data
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error adding student: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });


    }
}