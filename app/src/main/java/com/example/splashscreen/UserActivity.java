package com.example.splashscreen;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.splashscreen.adapter.UserTaskAdapter;
import com.example.splashscreen.model.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserActivity extends AppCompatActivity {

    private RecyclerView rvTasks;
    private TextView tvEmptyTasks;
    private ArrayList<Task> taskList;
    private UserTaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        rvTasks = findViewById(R.id.rvTasks);
        tvEmptyTasks = findViewById(R.id.tvEmptyTasks);

        taskList = new ArrayList<>();
        adapter = new UserTaskAdapter(taskList, (task, isCompleted) -> {
            // Ketika task dicentang, panggil method untuk update status
            updateTaskStatus(task, isCompleted);
        });

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(adapter);

        // Ambil data dari database saat activity dibuka
        ambilTugasDariDatabase();
    }

    private void ambilTugasDariDatabase() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/ToDoList/get_task.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                conn.disconnect();

                JSONArray jsonArray = new JSONArray(response.toString());

                taskList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String title = obj.getString("title");
                    String description = obj.getString("description");
                    String deadline = obj.getString("deadline");
                    // Tambahkan status completed (default: false)
                    boolean completed = obj.has("completed") && obj.getString("completed").equals("1");
                    Task task = new Task(title, description, deadline);
                    task.setCompleted(completed);
                    taskList.add(task);
                }

                runOnUiThread(() -> {
                    if (taskList.isEmpty()) {
                        tvEmptyTasks.setVisibility(View.VISIBLE);
                        rvTasks.setVisibility(View.GONE);
                    } else {
                        tvEmptyTasks.setVisibility(View.GONE);
                        rvTasks.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Gagal memuat data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    tvEmptyTasks.setVisibility(View.VISIBLE);
                    rvTasks.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private void updateTaskStatus(Task task, boolean isCompleted) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/ToDoList/update_task_status.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String data = "title=" + URLEncoder.encode(task.title, "UTF-8") +
                        "&deadline=" + URLEncoder.encode(task.deadline, "UTF-8") +
                        "&completed=" + (isCompleted ? "1" : "0");

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data);
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Toast.makeText(this, "Status tugas berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Gagal memperbarui status tugas", Toast.LENGTH_SHORT).show();
                    }
                });

                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
