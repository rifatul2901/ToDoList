package com.example.splashscreen;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class Splashscreen extends AppCompatActivity {
    // Durasi splash screen (dalam milidetik)
    private static int splashInterval = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Menghilangkan title bar dan membuat full screen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splashscreen);

        // Handler untuk delay sebelum pindah ke halaman lain
        new Handler().postDelayed(() -> {
            Intent i = new Intent(Splashscreen.this, WelcomeActivity.class); // Ganti ke LoginActivity jika perlu
            startActivity(i);
            finish(); // Menutup Splashscreen agar tidak bisa kembali ke sini
        }, splashInterval);
    }
}
