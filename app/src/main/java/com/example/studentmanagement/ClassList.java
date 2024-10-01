package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClassList extends AppCompatActivity {

    FloatingActionButton Classfab;
    FirebaseFirestore db;
    RecyclerView recyclerView1;
    List<Classes> classList; // Update to List<Classes>
    ClassAdapter adapter;
    LinearLayout studList, studAttendance,Report,editClass,DeleteClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        studList = findViewById(R.id.studList);
        studAttendance = findViewById(R.id.studAttnd);
        Report = findViewById(R.id.report);
        editClass = findViewById(R.id.editClass);
        DeleteClass = findViewById(R.id.deleteClass);
        recyclerView1 = findViewById(R.id.recyclerView_class);
        Classfab = findViewById(R.id.fab_class);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ClassList.this, 1);
        recyclerView1.setLayoutManager(gridLayoutManager);

        classList = new ArrayList<>(); // Initialize the list
        adapter = new ClassAdapter(classList); // Use the ClassAdapter
        recyclerView1.setAdapter(adapter);

        loadClassData(); // Load class data from Firestore


        Classfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClassList.this, AddClass.class);
                startActivity(intent);
            }
        });
    }

    private void loadClassData() {
        db = FirebaseFirestore.getInstance();
        db.collection("classes") // Adjust this to match your Firestore collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        classList.clear(); // Clear existing data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String className = document.getString("className"); // Use the correct field name from Firestore
                            classList.add(new Classes(className)); // Add the new class
                        }
                        adapter.notifyDataSetChanged(); // Notify adapter of changes
                    }
                });
    }
}
