package com.example.studentmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<Student> students;
    private OnStudentEditListener editListener;
    private OnStudentDeleteListener deleteListener;

    // Constructor for the adapter
    public StudentAdapter(List<Student> students, OnStudentEditListener editListener, OnStudentDeleteListener deleteListener) {
        this.students = students;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item (activity_item_student.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the student data at the current position
        Student student = students.get(position);

        // Bind the data to the views
        holder.name.setText(student.getName());
        holder.roll.setText("Roll: " + student.getRoll());

        // Set click listeners for edit and delete
        holder.editIcon.setOnClickListener(v -> editListener.onEdit(student));
        holder.deleteIcon.setOnClickListener(v -> deleteListener.onDelete(student));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    // ViewHolder class to hold the views
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, roll;
        ImageView editIcon, deleteIcon;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.studentName);
            roll = itemView.findViewById(R.id.studentRoll);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }

    // Listener interfaces for edit and delete actions
    public interface OnStudentEditListener {
        void onEdit(Student student);
    }

    public interface OnStudentDeleteListener {
        void onDelete(Student student);
    }

    // Method to update the adapter with a filtered list
    public void searchDataList(List<Student> filteredList) {
        students = filteredList;
        notifyDataSetChanged();
    }
}
