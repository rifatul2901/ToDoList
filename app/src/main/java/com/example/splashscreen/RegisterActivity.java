package com.example.splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {

    EditText editName, editEmail, editPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Lengkapi semua data!", Toast.LENGTH_SHORT).show();
            } else {
                new RegisterTask(RegisterActivity.this).execute(name, email, password);
            }
        });

        TextView textLogin = findViewById(R.id.textLogin);
        textLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private static class RegisterTask extends AsyncTask<String, Void, String> {
        private final Context context;

        public RegisterTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String email = params[1];
            String password = params[2];

            try {
                String link = "http://10.0.2.2/ToDoList/register.php"; // Ganti sesuai alamat server kamu

                String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                return sb.toString();
            } catch (Exception e) {
                return "error|Terjadi kesalahan: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            String[] parts = result.split("\\|");

            if (parts[0].equals("success")) {
                Toast.makeText(context, parts[1], Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, LoginActivity.class));
                if (context instanceof AppCompatActivity) {
                    ((AppCompatActivity) context).finish();
                }
            } else {
                Toast.makeText(context, parts.length > 1 ? parts[1] : "Registrasi gagal", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
