package com.example.trailrunner.ui.home_like;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
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

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private List<LatLng> pathPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // 권한 체크 및 요청
        if (!checkLocationPermission()) {
            requestLocationPermission();
        }

        // pathPoints 데이터 받기
        double[] pathData = getIntent().getDoubleArrayExtra("pathPoints");
        if (pathData != null && pathData.length >= 2) {
            pathPoints = new ArrayList<>();
            for (int i = 0; i < pathData.length; i += 2) {
                pathPoints.add(new LatLng(pathData[i], pathData[i + 1]));
            }
        }

        // 지도 초기화
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
        }

        if (pathPoints != null && !pathPoints.isEmpty()) {
            // 경로 그리기
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(pathPoints)
                    .width(10)
                    .color(Color.BLUE);
            mMap.addPolyline(polylineOptions);

            // 경로가 모두 보이도록 카메라 이동
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : pathPoints) {
                builder.include(point);
            }
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

            // 네비게이션 시작
            startNavigation();
        }
    }

    private void startNavigation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                1,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        updateNavigation(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                }
        );
    }

    private void updateNavigation(LatLng currentLocation) {
        if (pathPoints == null || pathPoints.isEmpty()) return;

        // 가장 가까운 경로 포인트 찾기
        LatLng nearestPoint = findNearestPoint(currentLocation);
        int currentIndex = pathPoints.indexOf(nearestPoint);

        if (currentIndex < pathPoints.size() - 1) {
            LatLng nextPoint = pathPoints.get(currentIndex + 1);

            // 다음 포인트까지의 거리와 방향 계산
            float[] results = new float[2];
            Location.distanceBetween(
                    currentLocation.latitude, currentLocation.longitude,
                    nextPoint.latitude, nextPoint.longitude,
                    results
            );

            // 안내 메시지 표시
            String guidance = String.format("%.0f미터 앞으로 이동하세요", results[0]);
            TextView guidanceText = findViewById(R.id.guidanceText);
            guidanceText.setText(guidance);
        }
    }

    private LatLng findNearestPoint(LatLng current) {
        if (pathPoints == null || pathPoints.isEmpty()) return null;

        LatLng nearest = pathPoints.get(0);
        double minDistance = getDistance(current, nearest);

        for (LatLng point : pathPoints) {
            double distance = getDistance(current, point);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = point;
            }
        }

        return nearest;
    }

    private double getDistance(LatLng point1, LatLng point2) {
        float[] results = new float[1];
        Location.distanceBetween(
                point1.latitude, point1.longitude,
                point2.latitude, point2.longitude,
                results
        );
        return results[0];
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인됨
                if (mMap != null) {
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                }
            } else {
                // 권한이 거부됨
                Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        }
    }
}