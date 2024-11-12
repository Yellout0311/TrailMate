package com.example.trailrunner.ui.running;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.trailrunner.R;

import java.util.Locale;

public class RunningResultActivity extends AppCompatActivity {
    private TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_result);

        tvTime = findViewById(R.id.tv_time);
        Button btnCreateCourse = findViewById(R.id.btn_create_course);
        Button btnExit = findViewById(R.id.btn_exit);

        // 전달받은 시간 표시
        long timeElapsed = getIntent().getLongExtra("TIME_ELAPSED", 0);
        int seconds = (int) (timeElapsed / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        tvTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));

        btnCreateCourse.setOnClickListener(v -> {
            // CourseRegisterActivity로 전환
            Intent intent = new Intent(this, CourseRegisterActivity.class);
            startActivity(intent);
            finish();
        });

        btnExit.setOnClickListener(v -> {
            finish();
        });
    }
}