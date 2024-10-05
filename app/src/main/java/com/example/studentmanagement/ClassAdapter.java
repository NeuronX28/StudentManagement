package com.example.studentmanagement;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private List<Classes> classList;
    private Context context;

    // Constructor
    public ClassAdapter(Context context, List<Classes> classList) {
        this.context = context;
        this.classList = classList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Classes currentClass = classList.get(position);
        holder.className.setText(currentClass.getClassName_class());

        // Set click listener for each layout in the card view
        holder.studList.setOnClickListener(v -> {
            Intent intent = new Intent(context, StudentActivity.class);
            intent.putExtra("className", currentClass.getClassName_class());
            context.startActivity(intent);
        });

        holder.studAttendance.setOnClickListener(v -> {
            //Intent intent = new Intent(context, AttendanceActivity.class);
            //intent.putExtra("className", currentClass.getClassName_class());
            //context.startActivity(intent);
        });

        holder.report.setOnClickListener(v -> {
            //Intent intent = new Intent(context, ReportActivity.class);
            //intent.putExtra("className", currentClass.getClassName_class());
            //context.startActivity(intent);
        });

        holder.editClass.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditClass.class);
            intent.putExtra("CLASS_ID", currentClass.getClassId()); // Pass the class ID
            context.startActivity(intent);
        });

        // Handle delete button click
        holder.deleteClass.setOnClickListener(v -> {
            deleteClass(currentClass.getClassId(), position); // Call the delete method
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public void searchDataList(List<Classes> searchList) {
        this.classList = searchList;
    }

    // Method to delete a class from Firestore and RecyclerView
    private void deleteClass(String classId, int position) {
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Delete the class document from Firestore
        db.collection("classes").document(classId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove the class from the local list
                    classList.remove(position);

                    // Notify the adapter of the change
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, classList.size());

                    Toast.makeText(context, "Class deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error deleting class: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView className;
        LinearLayout studList, studAttendance, report, editClass, deleteClass;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.class_name);
            studList = itemView.findViewById(R.id.studList);
            studAttendance = itemView.findViewById(R.id.studAttnd);
            report = itemView.findViewById(R.id.report);
            editClass = itemView.findViewById(R.id.editClass);
            deleteClass = itemView.findViewById(R.id.deleteClass);  // Ensure this button exists in your XML layout
        }
    }
}
