package com.example.trailrunner.ui.running;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.trailrunner.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseRegisterActivity extends AppCompatActivity {

    private List<LatLng> routePoints;
    private double totalDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_register);

        totalDistance = getIntent().getDoubleExtra("TOTAL_DISTANCE", 0.0);
        routePoints = getIntent().getParcelableArrayListExtra("ROUTE_POINTS");

        Spinner difficultySpinner = findViewById(R.id.spinner_difficulty);
        Spinner visibilitySpinner = findViewById(R.id.spinner_visibility);
        Button btnUpload = findViewById(R.id.btn_upload);
        TextView textView = findViewById(R.id.total_distance_textview);
        textView.setText("거리: " + totalDistance + "km");


        // 스피너 설정
        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.difficulty_levels,
                android.R.layout.simple_spinner_item
        );
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);

        ArrayAdapter<CharSequence> visibilityAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.visibility_options,
                android.R.layout.simple_spinner_item
        );
        visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visibilitySpinner.setAdapter(visibilityAdapter);

        btnUpload.setOnClickListener(v -> {
            uploadCourse();
        });
    }

    private void uploadCourse() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // LatLng 리스트를 Map 리스트로 변환
        List<Map<String, Double>> routePointsData = new ArrayList<>();
        for (LatLng point : routePoints) {
            Map<String, Double> pointData = new HashMap<>();
            pointData.put("latitude", point.latitude);
            pointData.put("longitude", point.longitude);
            routePointsData.add(pointData);
        }

        // 업로드할 데이터 준비
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("distance", totalDistance);
        courseData.put("routePoints", routePointsData);
        courseData.put("difficulty", ((Spinner) findViewById(R.id.spinner_difficulty)).getSelectedItem().toString());
        courseData.put("visibility", ((Spinner) findViewById(R.id.spinner_visibility)).getSelectedItem().toString());

        // Firestore에 데이터 업로드
        db.collection("courses")
                .add(courseData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Course uploaded successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload course: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}