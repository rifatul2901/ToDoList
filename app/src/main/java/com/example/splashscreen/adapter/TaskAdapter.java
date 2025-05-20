package com.example.splashscreen.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.splashscreen.R;
import com.example.splashscreen.model.Task;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> taskList;
    private OnTaskListener listener;

    public TaskAdapter(ArrayList<Task> taskList, OnTaskListener listener) {
        this.taskList = taskList;
        this.listener = listener;
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
        Task task = taskList.get(position);
        holder.txtTitle.setText(task.title);
        holder.txtDeadline.setText(task.deadline);

        holder.itemView.setOnClickListener(v -> listener.onTaskClick(position));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onTaskLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDeadline;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.textTitle);
            txtDeadline = itemView.findViewById(R.id.textDeadline);
        }
    }

    public interface OnTaskListener {
        void onTaskClick(int position);
        void onTaskLongClick(int position);
    }
}
