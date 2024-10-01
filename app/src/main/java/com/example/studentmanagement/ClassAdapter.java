package com.example.studentmanagement;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
            //Intent intent = new Intent(context, EditClassActivity.class);
            //intent.putExtra("className", currentClass.getClassName_class());
            //context.startActivity(intent);
        });

        holder.deleteClass.setOnClickListener(v -> {
            //Intent intent = new Intent(context, DeleteClassActivity.class);
            //intent.putExtra("className", currentClass.getClassName_class());
            //context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public void searchDataList(List<Classes> searchList) {
        this.classList = searchList;
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
            deleteClass = itemView.findViewById(R.id.deleteClass);
        }
    }
}
