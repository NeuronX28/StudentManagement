package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends AppCompatActivity {
    FloatingActionButton fab;
    FirebaseFirestore db;
    RecyclerView recyclerView;
    List<Student> studentList;
    StudentAdapter adapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(StudentActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        studentList = new ArrayList<>();
        adapter = new StudentAdapter(studentList, this::editStudent, this::deleteStudent);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadStudentData();

        // Set search functionality
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentActivity.this, upload_Activity.class);
                startActivity(intent);
            }
        });
    }

    private void loadStudentData() {
        db.collection("students")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        studentList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String roll = document.getString("roll");
                            String name = document.getString("name");
                            String className = document.getString("class");
                            String program = document.getString("program");

                            studentList.add(new Student(roll, name, className, program));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public void searchList(String text) {
        ArrayList<Student> searchList = new ArrayList<>();
        for (Student student : studentList) {
            if (student.getName().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(student);
            }
        }
        adapter.searchDataList(searchList);
    }

    private void editStudent(Student student) {
        // Code to edit student
    }

    private void deleteStudent(Student student) {
        // Code to delete student
    }
}
