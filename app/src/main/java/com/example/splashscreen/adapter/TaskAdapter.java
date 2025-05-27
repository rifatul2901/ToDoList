package com.example.splashscreen.adapter;

import android.graphics.Color;
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
        holder.txtDescription.setText(task.description);

        // Tampilkan status completed dan ubah tampilan
        if (task.isCompleted()) {
            // Jika tugas sudah selesai
            holder.txtStatus.setText("✓ SELESAI");
            holder.txtStatus.setVisibility(View.VISIBLE);

            // Ubah warna background menjadi sedikit gelap dengan transparansi
            holder.itemView.setAlpha(0.6f);

            // Ubah warna teks menjadi lebih gelap
            holder.txtTitle.setTextColor(Color.parseColor("#757575"));
            holder.txtDeadline.setTextColor(Color.parseColor("#9E9E9E"));
            holder.txtDescription.setTextColor(Color.parseColor("#9E9E9E"));

            // Background status
            holder.txtStatus.setBackgroundResource(R.drawable.bg_completed_status);
            holder.txtStatus.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            // Jika tugas belum selesai
            holder.txtStatus.setVisibility(View.GONE);

            // Kembalikan ke tampilan normal
            holder.itemView.setAlpha(1.0f);
            holder.txtTitle.setTextColor(Color.parseColor("#212121"));
            holder.txtDeadline.setTextColor(Color.parseColor("#FF5722"));
            holder.txtDescription.setTextColor(Color.parseColor("#424242"));
        }

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
        TextView txtTitle, txtDeadline, txtDescription, txtStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.textTitle);
            txtDeadline = itemView.findViewById(R.id.textDeadline);
            txtDescription = itemView.findViewById(R.id.txtTask);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }

    public interface OnTaskListener {
        void onTaskClick(int position);
        void onTaskLongClick(int position);
    }
}