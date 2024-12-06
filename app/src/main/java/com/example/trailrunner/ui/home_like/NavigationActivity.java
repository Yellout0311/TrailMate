package com.example.trailrunner.ui.home_like;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trailrunner.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FirebaseFirestore db;
    private List<LatLng> pathPoints = new ArrayList<>();
    private TextView guidanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        guidanceText = findViewById(R.id.guidanceText);

        // 권한 체크 및 요청
        if (!checkLocationPermission()) {
            requestLocationPermission();
        }

        // Firebase Firestore 초기화 및 데이터 로드
        db = FirebaseFirestore.getInstance();
        String courseId = getIntent().getStringExtra("COURSE_ID");
        loadTrackData(courseId);

        // 지도 초기화
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void loadTrackData(String courseId) {
        if (courseId == null || courseId.isEmpty()) {
            return;
        }

        db.collection("courses").document(courseId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        parseRoutePoints(documentSnapshot);
                        if (mMap != null && !pathPoints.isEmpty()) {
                            // 데이터 로드 후 mMap이 초기화되어 있으면 지도 업데이트
                            onMapReady(mMap);
                        }
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }


    private void parseRoutePoints(DocumentSnapshot documentSnapshot) {
        List<Map<String, Double>> points = (List<Map<String, Double>>) documentSnapshot.get("routePoints");
        if (points != null) {
            for (Map<String, Double> point : points) {
                double latitude = point.get("latitude");
                double longitude = point.get("longitude");
                pathPoints.add(new LatLng(latitude, longitude));
            }
        } else {
            Toast.makeText(this, "경로 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
        }

        // pathPoints가 비어 있지 않으면 지도를 업데이트
        if (pathPoints != null && !pathPoints.isEmpty()) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(pathPoints)
                    .width(10)
                    .color(Color.BLUE);
            mMap.addPolyline(polylineOptions);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : pathPoints) {
                builder.include(point);
            }
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

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
                    public void onLocationChanged(@NonNull Location location) {
                        updateNavigation(new LatLng(location.getLatitude(), location.getLongitude()));
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        Toast.makeText(NavigationActivity.this, "GPS가 비활성화되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void updateNavigation(LatLng currentLocation) {
        if (pathPoints == null || pathPoints.isEmpty()) return;

        LatLng nearestPoint = findNearestPoint(currentLocation);
        int currentIndex = pathPoints.indexOf(nearestPoint);

        if (currentIndex < pathPoints.size() - 1) {
            LatLng nextPoint = pathPoints.get(currentIndex + 1);

            float[] results = new float[2];
            Location.distanceBetween(
                    currentLocation.latitude, currentLocation.longitude,
                    nextPoint.latitude, nextPoint.longitude,
                    results
            );

            Log.d("LocationDebug", "Current Location: " + currentLocation.latitude + ", " + currentLocation.longitude);
            Log.d("LocationDebug", "Next Point: " + nextPoint.latitude + ", " + nextPoint.longitude);
            Log.d("DistanceDebug", "Calculated distance: " + results[0] + " meters");

            String guidance = String.format("%.0f미터 앞으로 이동하세요", results[0]);
            guidanceText.setText(guidance);
        } else {
            guidanceText.setText("목적지에 도착했습니다.");
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
                if (mMap != null) {
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                }
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
