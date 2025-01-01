package com.hadyaddien.todolistapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    // List untuk menampung semua data tugas
    private List<Task> tasks;
    private OnItemClickListener listener;

    // Constructor
    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    // ViewHolder untuk setiap item dalam RecyclerView
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle, textViewDescription;
        private CheckBox checkBoxTask;
        private Button buttonEdit, buttonDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            checkBoxTask = itemView.findViewById(R.id.checkbox_task);
            buttonEdit = itemView.findViewById(R.id.button_edit);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.textViewTitle.setText(currentTask.getTitle());
        holder.textViewDescription.setText(currentTask.getDescription());
        holder.checkBoxTask.setChecked(currentTask.isCompleted());

        // Event jika user melakukan klik view detail
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentTask);
            }
        });

        holder.checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onTaskChecked(currentTask, isChecked);
            }
        });
        holder.buttonEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemEdit(currentTask);
            }
        });

        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemDelete(currentTask);
            }
        });
    }



    @Override
    public int getItemCount() {
        return tasks.size();
    }

    // Interface untuk menangani event klik item
    public interface OnItemClickListener {
        void onItemClick(Task task);
        void onTaskChecked(Task task, boolean isChecked);
        void onItemEdit(Task task);
        void onItemDelete(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Method untuk memperbarui data di RecyclerView
    public void setTasks(List<Task> tasks) {
        if (tasks != null) {
            this.tasks = tasks;
            notifyDataSetChanged();
        }
    }



}
