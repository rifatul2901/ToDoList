package com.example.splashscreen;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.splashscreen.adapter.TaskAdapter;
import com.example.splashscreen.model.Task;

import java.io.*;
import java.net.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerTasks;
    private Button btnAddTask;
    private ArrayList<Task> taskList;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        recyclerTasks = findViewById(R.id.recyclerTasks);
        btnAddTask = findViewById(R.id.btnAddTask);

        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskListener() {
            @Override
            public void onTaskClick(int position) {
                tampilkanDetailTugas(position);
            }

            @Override
            public void onTaskLongClick(int position) {
                tampilKonfirmasiHapus(position);
            }
        });

        recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerTasks.setAdapter(adapter);

        btnAddTask.setOnClickListener(v -> tampilFormTambahTugas());

        // Ambil data dari database saat activity dibuka
        ambilTugasDariDatabase();
    }

    private void tampilFormTambahTugas() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText inputJudul = view.findViewById(R.id.edtTitle);
        EditText inputDeskripsi = view.findViewById(R.id.edtDesc);
        Button btnDeadline = view.findViewById(R.id.btnDeadline);

        final String[] deadlineDipilih = {""};

        btnDeadline.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view1, year1, month1, dayOfMonth) -> {
                        String tanggal = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        deadlineDipilih[0] = tanggal;
                        btnDeadline.setText("Deadline: " + tanggal);
                    }, year, month, day);
            datePickerDialog.show();
        });

        new AlertDialog.Builder(this)
                .setTitle("Tambah Tugas")
                .setView(view)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String judul = inputJudul.getText().toString().trim();
                    String deskripsi = inputDeskripsi.getText().toString().trim();
                    String deadline = deadlineDipilih[0];

                    if (judul.isEmpty() || deskripsi.isEmpty() || deadline.isEmpty()) {
                        Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
                    } else {
                        Task task = new Task(judul, deskripsi, convertToSqlDate(deadline));
                        taskList.add(task);
                        adapter.notifyItemInserted(taskList.size() - 1);
                        simpanTugasKeDatabase(task);
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void tampilkanDetailTugas(int position) {
        Task task = taskList.get(position);
        String detail = "Judul: " + task.title + "\nDeskripsi: " + task.description + "\nDeadline: " + task.deadline;
        new AlertDialog.Builder(this)
                .setTitle("Detail Tugas")
                .setMessage(detail)
                .setPositiveButton("OK", null)
                .show();
    }

    private void tampilKonfirmasiHapus(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Tugas")
                .setMessage("Apakah kamu yakin ingin menghapus tugas ini?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    Task task = taskList.get(position);
                    taskList.remove(position);
                    adapter.notifyItemRemoved(position);
                    hapusTugasKeDatabase(task);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void simpanTugasKeDatabase(Task task) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/ToDoList/insert.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String data = "title=" + URLEncoder.encode(task.title, "UTF-8") +
                        "&description=" + URLEncoder.encode(task.description, "UTF-8") +
                        "&deadline=" + URLEncoder.encode(task.deadline, "UTF-8");

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data);
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Toast.makeText(this, "Tugas berhasil disimpan ke database", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Gagal menyimpan tugas", Toast.LENGTH_SHORT).show();
                    }
                });

                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
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

                    taskList.add(new Task(title, description, deadline));
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Gagal memuat data: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void hapusTugasKeDatabase(Task task) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/ToDoList/delete_task.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String data = "title=" + URLEncoder.encode(task.title, "UTF-8") +
                        "&deadline=" + URLEncoder.encode(task.deadline, "UTF-8");

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data);
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                runOnUiThread(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Toast.makeText(this, "Tugas berhasil dihapus dari database", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Gagal menghapus tugas", Toast.LENGTH_SHORT).show();
                    }
                });

                conn.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error saat hapus: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private String convertToSqlDate(String tanggalIndo) {
        String[] parts = tanggalIndo.split("/");
        return parts[2] + "-" + parts[1] + "-" + parts[0];
    }
}