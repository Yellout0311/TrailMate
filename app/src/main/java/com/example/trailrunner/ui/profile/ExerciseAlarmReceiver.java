package com.example.trailrunner.ui.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.Manifest;
import com.example.trailrunner.R;

public class ExerciseAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TRAILRUNNER_CHANNEL")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("운동 시간")
                .setContentText("오늘의 운동을 시작할 시간입니다!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_ALARM)  // 알람 카테고리로 설정
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);  // 잠금화면에서도 표시

        try {
            NotificationManagerCompat.from(context).notify(1, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}