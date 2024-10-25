package com.example.studentmanagement;

import static kotlin.text.Typography.section;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        recyclerView1 = findViewById(R.id.recyclerView_class);
        Classfab = findViewById(R.id.fab_class);

        // Set up the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNav);

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
            finish();
        });

        bottomNavigationView.setSelectedItemId(R.id.navclasses);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String str= item.getTitle().toString();
                switch (str) {
                    case "Classes":
                        return true;

                    case "Settings":
                        navToSettings();
                        return true;

                    case "Profile":
                       navigateToProfile();
                        return true;

                    default:
                        return false;
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClassData(); // Reload class data when coming back to this activity
    }

    private void loadClassData() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        db.collection("classes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        classList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String classId = document.getId();
                            String className = document.getString("className");
                            String section = document.getString("section");
                            classList.add(new Classes(classId, className,section));
                        }
                adapter.notifyDataSetChanged();
    }
});
        }

private void navToSettings() {
    Intent settingsIntent = new Intent(ClassList.this, Settings.class);
        startActivity(settingsIntent);
        finish();
    }

    private void navigateToClasses() {
        Intent classListIntent = new Intent(ClassList.this, ClassList.class);
        startActivity(classListIntent);
        finish();
    }


    private void navigateToProfile() {
        Intent profileIntent = new Intent(ClassList.this, ProfileActivity.class);
        startActivity(profileIntent);
        finish();
    }
}
