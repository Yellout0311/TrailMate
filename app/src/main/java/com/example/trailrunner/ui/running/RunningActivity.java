package com.example.trailrunner.ui.running;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trailrunner.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.ArrayList;
import java.util.List;
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
    private FusedLocationProviderClient fusedLocationClient; // 현재위치 가져오는 client
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    private float totalDistance = 0f;
    private TextView distanceTextView;
    private TextView speedTextView;
    private TextView caloriesTextView;
    private float currentSpeed = 0f;
    private float totalCalories = 0f;
    private final float userWeight = 70f; // 사용자 체중

    // 경로 추적을 위한 변수들
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private List<LatLng> pathPoints;
    private PolylineOptions polylineOptions;

    private void initializeMap() {
        Log.d("MapDebug", "Initializing map");
        SupportMapFragment mapFragment = new SupportMapFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map, mapFragment)
                .commit();

        mapFragment.getMapAsync(callback -> {
            Log.d("MapDebug", "Map is ready");
            mMap = callback;
            if(checkLocationPermission()) {
                setupMap();
            } else {
                requestLocationPermission();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        btnPlayStop = findViewById(R.id.btn_play_stop);
        btnRetry = findViewById(R.id.btn_retry);
        timerTextView = findViewById(R.id.timer_text);

        //위치,거리 서비스 초기화 + 움직인 거리, 속도, 칼로리
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        distanceTextView = findViewById(R.id.distance_text_view);
        speedTextView = findViewById(R.id.speed_text_view);
        caloriesTextView = findViewById(R.id.calories_text_view);
        //경로 추적 초기화
        pathPoints = new ArrayList<>();
        polylineOptions = new PolylineOptions()
                .color(Color.BLUE)
                        .width(12f);
        //위치 요청 설정
        createLocationRequest();
        createLocationCallback();

        // 지도 초기화 - 기존 코드를 이 한 줄로 대체
        initializeMap();

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

    private float calculateDistance(LatLng point1, LatLng point2) {
        float[] results = new float[1];
        Location.distanceBetween(
                point1.latitude, point1.longitude,
                point2.latitude, point2.longitude,
                results
        );
        return results[0]; // 미터 단위 거리
    }

    private void createLocationRequest(){
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000)
                .setMinUpdateDistanceMeters(5) //5밈터마다 업데이트
                .build();
    }
    private LatLng previousLocation = null; // 이전 위치 저장용 변수 추가
    private long previousLocationTime = 0;
    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (!isRunning){
                    return;
            }

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    LatLng currentPoint = new LatLng(location.getLatitude(), location.getLongitude());

                    Log.d("LocationUpdate", "Location received: " + currentPoint);
                    // 러닝 통계 업데이트
                    updateRunningStats(currentPoint, location);

                    // 이전 위치가 있으면 선 그리기
                    if (previousLocation != null) {
                        mMap.addPolyline(new PolylineOptions()
                                .color(Color.BLUE)  // 선 색상
                                .width(15f)        // 선 두께
                                .geodesic(true)    // 곡선으로 그리기
                                .add(previousLocation)  // 이전 위치
                                .add(currentPoint));    // 현재 위치
                    }

                    // 현재 위치를 이전 위치로 저장 + 카메라를 현재 위치로 이동
                    previousLocation = currentPoint;
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(currentPoint));;
                }
            }
        };
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("MapDebug","Map is ready");

        if(checkLocationPermission()) {
            setupMap();
        }else {
            requestLocationPermission();
        }
    }
    private void startLocationUpdates() {
        if (checkLocationPermission()) {
            try {
                fusedLocationClient.requestLocationUpdates(locationRequest,
                        locationCallback,
                        Looper.getMainLooper());
            } catch (SecurityException e) {
                Log.e("Location", "Error starting location updates", e);
            }
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION //정밀한 위치
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION //대략적인 위치
                },
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }
    private void setupMap() {
        if (checkLocationPermission()) {
            try {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                // 현재 위치 가져오기
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                LatLng currentLatLng = new LatLng(
                                        location.getLatitude(),
                                        location.getLongitude()
                                );

                                // 지도 카메라 이동
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        currentLatLng,
                                        15f
                                ));
                            }
                        });
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupMap();
            } else {
                Toast.makeText(
                        this,
                        "위치 권한이 필요합니다",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void startRunning() {
        isRunning = true;
        btnPlayStop.setImageResource(R.drawable.stop);
        btnRetry.setVisibility(View.VISIBLE);
        startTime = System.currentTimeMillis() - elapsedTime;
        startTimer();
        startLocationUpdates(); //위치 업데이트 시작
    }

    private void stopRunning() {
        isRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
        stopLocationUpdates(); //위치 업데이트 중지

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

        // 통계 정보 리셋
        totalDistance = 0f;
        currentSpeed = 0f;
        totalCalories = 0f;
        previousLocation = null;
        previousLocationTime = 0;

        // UI 업데이트
        if (distanceTextView != null)
            distanceTextView.setText("거리: 0.00 km");
        if (speedTextView != null)
            speedTextView.setText("속도: 0.0 km/h");
        if (caloriesTextView != null)
            caloriesTextView.setText("칼로리: 0.0 kcal");

        // 지도 초기화
        if (mMap != null) {
            mMap.clear();
        }
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

    //거리, 속도, 칼로리 계산 + 실시간 업데이트
    private void updateRunningStats(LatLng currentLocation, Location location) {
        if (previousLocation != null) {
            float segmentDistance = calculateDistance(previousLocation, currentLocation);
            totalDistance += segmentDistance;

            // 속도 계산 (m/s에서 km/h로)
            float timeDiff = location.getTime() - previousLocationTime;
            currentSpeed = (segmentDistance / (timeDiff / 1000f)) * 3.6f;

            float timeDiffHours = timeDiff / (1000f * 60 * 60);
            float caloriesBurned = 7f * userWeight * timeDiffHours;
            totalCalories += caloriesBurned;

            // UI 업데이트
            updateStatsDisplay();
        }

        previousLocationTime = location.getTime();
    }
    // 통계 정보 디스플레이 업데이트 메서드
    private void updateStatsDisplay() {
        Log.d("RunningStats", String.format("Distance: %.2f, Speed: %.1f, Calories: %.1f",
                totalDistance / 1000f, currentSpeed, totalCalories));

        if (distanceTextView != null) {
            distanceTextView.setText(String.format(Locale.getDefault(),
                    "거리: %.2f km", totalDistance / 1000f));
        }

        if (speedTextView != null) {
            speedTextView.setText(String.format(Locale.getDefault(),
                    "속도: %.1f km/h", currentSpeed));
        }

        if (caloriesTextView != null) {
            caloriesTextView.setText(String.format(Locale.getDefault(),
                    "칼로리: %.1f kcal", totalCalories));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
        if(isRunning) {
            stopLocationUpdates();
        }
    }

}