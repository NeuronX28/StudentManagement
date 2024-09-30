package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class showActivity extends AppCompatActivity {

    Button btnBack;
    private FirebaseFirestore db;
    private TextView displayData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        db = FirebaseFirestore.getInstance();
        displayData = findViewById(R.id.displayData);
        btnBack = findViewById(R.id.btnBack); // Find the back button in the layout

        // Load data from Firestore
        loadDataFromFirestore();

        // Set up the back button click listener
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(showActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close this activity
        });
    }

    private void loadDataFromFirestore() {
        db.collection("students")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder dataBuilder = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String roll = document.getString("roll");
                            String name = document.getString("name");
                            String className = document.getString("class");
                            String program = document.getString("program");

                            dataBuilder.append("Roll: ").append(roll).append("\n")
                                    .append("Name: ").append(name).append("\n")
                                    .append("Class: ").append(className).append("\n")
                                    .append("Program: ").append(program).append("\n\n");
                        }
                        displayData.setText(dataBuilder.toString());
                    } else {
                        Toast.makeText(this, "Error loading data: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
