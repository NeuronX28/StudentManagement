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
    List<Classes> classList;
    ClassAdapter adapter;
    ImageView settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        recyclerView1 = findViewById(R.id.recyclerView_class);
        Classfab = findViewById(R.id.fab_class);
        settings = findViewById(R.id.imageView3);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ClassList.this, 1);
        recyclerView1.setLayoutManager(gridLayoutManager);

        classList = new ArrayList<>();
        adapter = new ClassAdapter(this, classList);
        recyclerView1.setAdapter(adapter);

        loadClassData(); // Load class data from Firestore

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClassList.this, Settings.class);
                startActivity(intent);
            }
        });

        // Floating action button to add new class
        Classfab.setOnClickListener(view -> {
            Intent intent = new Intent(ClassList.this, AddClass.class);
            startActivity(intent);
        });
    }

    private void loadClassData() {
        db = FirebaseFirestore.getInstance();
        db.collection("classes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        classList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String className = document.getString("className");
                            classList.add(new Classes(className));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
