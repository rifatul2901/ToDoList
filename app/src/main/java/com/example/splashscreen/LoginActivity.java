package com.example.splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {

    EditText editUsername, editPassword;
    Button btnLogin;
    TextView textRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi komponen
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        textRegister = findViewById(R.id.textRegister);

        // Tombol login ditekan
        btnLogin.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "username dan password harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                new LoginTask(LoginActivity.this).execute(username, password);
            }
        });

        // Pindah ke halaman register
        textRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private static class LoginTask extends AsyncTask<String, Void, String> {
        private final Context context;

        public LoginTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            try {
                // Ganti dengan alamat URL login.php kamu
                String link = "http://10.0.2.2/ToDoList/login.php"; // Ganti sesuai IP/server kamu

                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
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
            // Tambahkan log debug
            Log.d("LOGIN_RESPONSE", "Server response: " + result);

            String[] parts = result.split("\\|");

            if (parts.length >= 4 && parts[0].equals("success")) {
                String role = parts[3]; // Mendapatkan role (admin atau user)

                Toast.makeText(context, "Login berhasil sebagai " + role, Toast.LENGTH_SHORT).show();

                Intent intent;
                if (role.equals("admin")) {
                    intent = new Intent(context, AdminActivity.class);
                } else {
                    intent = new Intent(context, UserActivity.class);
                }

                intent.putExtra("user_role", role);
                context.startActivity(intent);

                if (context instanceof AppCompatActivity) {
                    ((AppCompatActivity) context).finish();
                }
            } else {
                String errorMessage = parts.length >= 2 ? parts[1] : "Login gagal";
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }

    }
}
