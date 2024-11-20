package com.example.trailrunner.ui.running;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.trailrunner.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RunningResultActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView tvTime;
    private TextView tvDistance;
    private GoogleMap mMap;
    private List<LatLng> routePoints;
    private double totalDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_result);

        tvTime = findViewById(R.id.tv_time);
        tvDistance = findViewById(R.id.tv_distance);
        Button btnCreateCourse = findViewById(R.id.btn_create_course);
        Button btnExit = findViewById(R.id.btn_exit);

        // 맵 프래그먼트 초기화
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Intent에서 데이터 받기
        Intent intent = getIntent();
         // 전달받은 시간 표시
        long timeElapsed = intent.getLongExtra("TIME_ELAPSED", 0);
        int seconds = (int) (timeElapsed / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        tvTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));

        totalDistance = intent.getDoubleExtra("TOTAL_DISTANCE", 0.0);
        routePoints = intent.getParcelableArrayListExtra("ROUTE_POINTS");

        displayDistance(totalDistance);

        btnCreateCourse.setOnClickListener(v -> {
            Intent courseIntent = new Intent(this, CourseRegisterActivity.class);
            courseIntent.putParcelableArrayListExtra("ROUTE_POINTS", new ArrayList<>(routePoints));
            courseIntent.putExtra("TOTAL_DISTANCE", totalDistance);
            startActivity(courseIntent);
            finish();
        });

        btnExit.setOnClickListener(v -> {
            finish();
        });
    }

    private void displayDistance(double distance) {
        tvDistance.setText(String.format(Locale.getDefault(), "%.2f km",distance/1000));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (routePoints != null && !routePoints.isEmpty()) {
            // Polyline 그리기
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(routePoints)
                    .width(12)
                    .color(getResources().getColor(R.color.purple_500));
            mMap.addPolyline(polylineOptions);

            // 경로가 모두 보이도록 카메라 위치 조정
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : routePoints) {
                builder.include(point);
            }
            LatLngBounds bounds = builder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }
}