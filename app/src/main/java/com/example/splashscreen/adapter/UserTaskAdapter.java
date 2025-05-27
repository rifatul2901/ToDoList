package com.example.splashscreen.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.splashscreen.R;
import com.example.splashscreen.model.Task;

import java.util.ArrayList;

public class UserTaskAdapter extends RecyclerView.Adapter<UserTaskAdapter.TaskViewHolder> {

    private ArrayList<Task> taskList;
    private OnTaskStatusChangeListener listener;

    public interface OnTaskStatusChangeListener {
        void onTaskStatusChanged(Task task, boolean isCompleted);
    }

    public UserTaskAdapter(ArrayList<Task> taskList, OnTaskStatusChangeListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.txtTitle.setText(task.title);
        holder.txtDeadline.setText(String.format("Deadline: %s", task.deadline));
        holder.txtDescription.setText(task.description);
        holder.checkCompleted.setChecked(task.isCompleted());

        if (task.isCompleted()) {
            // Disable checkbox
            holder.checkCompleted.setEnabled(false);

            // Ubah tampilan jadi transparan dan teks warna abu-abu
            holder.itemView.setAlpha(0.6f);
            holder.txtTitle.setTextColor(Color.parseColor("#757575"));
            holder.txtDeadline.setTextColor(Color.parseColor("#9E9E9E"));
            holder.txtDescription.setTextColor(Color.parseColor("#9E9E9E"));
        } else {
            // Checkbox bisa dicentang
            holder.checkCompleted.setEnabled(true);

            // Tampilan normal
            holder.itemView.setAlpha(1.0f);
            holder.txtTitle.setTextColor(Color.parseColor("#212121"));
            holder.txtDeadline.setTextColor(Color.parseColor("#FF5722"));
            holder.txtDescription.setTextColor(Color.parseColor("#424242"));

            // Set listener hanya kalau belum selesai
            holder.checkCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setCompleted(isChecked);
                listener.onTaskStatusChanged(task, isChecked);
            });
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDeadline, txtDescription;
        CheckBox checkCompleted;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.textTitle);
            txtDeadline = itemView.findViewById(R.id.textDeadline);
            txtDescription = itemView.findViewById(R.id.textDescription);
            checkCompleted = itemView.findViewById(R.id.checkCompleted);
        }
    }
}