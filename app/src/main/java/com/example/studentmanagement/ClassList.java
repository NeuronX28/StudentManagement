package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    List<Classes> classList;
    ClassAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        recyclerView1 = findViewById(R.id.recyclerView_class);
        Classfab = findViewById(R.id.fab_class);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ClassList.this, 1);
        recyclerView1.setLayoutManager(gridLayoutManager);

        classList = new ArrayList<>();
        adapter = new ClassAdapter(this, classList);
        recyclerView1.setAdapter(adapter);

        loadClassData(); // Load class data from Firestore

        // Floating action button to add new class
        Classfab.setOnClickListener(view -> {
            Intent intent = new Intent(ClassList.this, AddClass.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClassData(); // Reload class data when coming back to this activity
    }

    private void loadClassData() {
        db = FirebaseFirestore.getInstance();
        db.collection("classes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        classList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the document ID (classId) and className from Firestore
                            String classId = document.getId();  // This is the document ID
                            String className = document.getString("className");  // Field in Firestore document

                            // Add the new Classes object with both classId and className
                            classList.add(new Classes(classId, className));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

}
