package com.example.trailrunner.ui.profile;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trailrunner.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    private NotificationManager notificationManager;
    private TimePicker exerciseTimePicker;
   // private Switch reviewNotificationSwitch;
    private AlarmManager alarmManager;
    private static final String CHANNEL_ID = "TRAILRUNNER_CHANNEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        createNotificationChannel();

        // 알림 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        123);
            }
        }

        exerciseTimePicker = findViewById(R.id.exerciseTimePicker);
       // reviewNotificationSwitch = findViewById(R.id.reviewNotificationSwitch);
        View btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(v -> finish());
        Button btnSaveTime = findViewById(R.id.btnSaveTime);
        btnSaveTime.setOnClickListener(v -> {
            int hour = exerciseTimePicker.getHour();
            int minute = exerciseTimePicker.getMinute();
            setExerciseAlarm(hour, minute);
            saveSettings();
            Toast.makeText(this, "알림 시간이 저장되었습니다", Toast.LENGTH_SHORT).show();
            updateAlarmStatus();
        });

        // 저장된 설정 불러오기
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        exerciseTimePicker.setHour(prefs.getInt("exercise_hour", 9));
        exerciseTimePicker.setMinute(prefs.getInt("exercise_minute", 0));
        //reviewNotificationSwitch.setChecked(prefs.getBoolean("review_notifications", true));

        exerciseTimePicker.setOnTimeChangedListener((view, hour, minute) -> {
            setExerciseAlarm(hour, minute);
            saveSettings();
        });

//        reviewNotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            enableReviewNotifications(isChecked);
//            saveSettings();
//        });
    }

    private void updateAlarmStatus() {
        TextView statusText = findViewById(R.id.alarmStatusText);
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        int hour = exerciseTimePicker.getHour();
        int minute = exerciseTimePicker.getMinute();
        statusText.setText(String.format("매일 %02d:%02d에 알림이 전송됩니다", hour, minute));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "TrailRunner Notifications",
                    NotificationManager.IMPORTANCE_HIGH  // HIGH로 변경
            );
            channel.setDescription("운동 알림");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setExerciseAlarm(int hour, int minute) {
        Intent intent = new Intent(this, ExerciseAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAlarmClock(
                        new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent),
                        pendingIntent
                );
            } else {
                Toast.makeText(this, "정확한 알람 권한이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }

        updateAlarmStatus();
    }

//    private void enableReviewNotifications(boolean enabled) {
//        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
//        prefs.edit().putBoolean("review_notifications", enabled).apply();
//    }

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putInt("exercise_hour", exerciseTimePicker.getHour());
        editor.putInt("exercise_minute", exerciseTimePicker.getMinute());
       // editor.putBoolean("review_notifications", reviewNotificationSwitch.isChecked());
        editor.apply();
    }
}