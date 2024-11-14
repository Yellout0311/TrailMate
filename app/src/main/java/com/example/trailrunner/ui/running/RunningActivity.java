package com.example.trailrunner.ui.running;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.trailrunner.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class RunningActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ImageButton btnPlayStop;
    private ImageButton btnRetry;
    private TextView timerTextView;
    private boolean isRunning = false;
    private long startTime = 0;
    private long elapsedTime = 0;
    private final Handler timerHandler = new Handler();
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        btnPlayStop = findViewById(R.id.btn_play_stop);
        btnRetry = findViewById(R.id.btn_retry);
        timerTextView = findViewById(R.id.timer_text);

        btnPlayStop.setOnClickListener(v -> {
            if (!isRunning) {
                startRunning();
            } else {
                stopRunning();
            }
        });

        btnRetry.setOnClickListener(v -> {
            resetRunning();
        });
    }

    private void startRunning() {
        isRunning = true;
        btnPlayStop.setImageResource(R.drawable.stop);
        btnRetry.setVisibility(View.VISIBLE);
        startTime = System.currentTimeMillis() - elapsedTime;
        startTimer();
    }

    private void stopRunning() {
        isRunning = false;
        timerHandler.removeCallbacks(timerRunnable);

        // ResultActivity로 전환하면서 시간 전달
        Intent intent = new Intent(this, RunningResultActivity.class);
        intent.putExtra("TIME_ELAPSED", elapsedTime);
        startActivity(intent);
        finish(); // 현재 Activity 종료
    }

    private void resetRunning() {
        isRunning = false;
        btnPlayStop.setImageResource(R.drawable.play);
        btnRetry.setVisibility(View.GONE);
        timerHandler.removeCallbacks(timerRunnable);
        elapsedTime = 0;
        timerTextView.setText("00:00:00");
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime;
                updateTimerText(elapsedTime);
                timerHandler.postDelayed(this, 1000);
            }
        }
    };

    private void updateTimerText(long timeInMillis) {
        int seconds = (int) (timeInMillis / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void startTimer() {
        timerRunnable.run();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}