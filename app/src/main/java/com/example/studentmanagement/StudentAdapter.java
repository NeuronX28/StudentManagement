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

    public StudentAdapter(List<Student> students, OnStudentEditListener editListener, OnStudentDeleteListener deleteListener) {
        this.students = students;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = students.get(position);
        holder.name.setText(student.getName());
        holder.roll.setText("Roll: " + student.getRoll());
        holder.className.setText("Class: " + student.getClassName());
        holder.program.setText("Program: " + student.getProgram());

        holder.editIcon.setOnClickListener(v -> editListener.onEdit(student));
        holder.deleteIcon.setOnClickListener(v -> deleteListener.onDelete(student));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void searchDataList(List<Student> searchList) {
        this.students = searchList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, roll, className, program;
        ImageView editIcon, deleteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.studentName);
            roll = itemView.findViewById(R.id.studentRoll);
            className = itemView.findViewById(R.id.studentClass);
            program = itemView.findViewById(R.id.studentProgram);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }

    public interface OnStudentEditListener {
        void onEdit(Student student);
    }

    public interface OnStudentDeleteListener {
        void onDelete(Student student);
    }
}
