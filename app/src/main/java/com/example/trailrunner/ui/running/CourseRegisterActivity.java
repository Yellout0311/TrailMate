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
    private TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_register);

        // Intent로 전달받은 데이터
        totalDistance = getIntent().getDoubleExtra("TOTAL_DISTANCE", 0.0);
        routePoints = getIntent().getParcelableArrayListExtra("ROUTE_POINTS");

        // UI 요소 초기화
        Spinner difficultySpinner = findViewById(R.id.spinner_difficulty);
        Spinner visibilitySpinner = findViewById(R.id.spinner_visibility);
        Button btnUpload = findViewById(R.id.btn_upload);
        TextView distanceTextView = findViewById(R.id.total_distance_textview);
        nameTextView = findViewById(R.id.et_course_name);

        // 이동거리 표시 (km 단위로 변환 및 포맷팅)
        totalDistance = totalDistance / 1000;
        String formattedDistance = String.format("%.1f", totalDistance);
        totalDistance = Double.parseDouble(formattedDistance);
        distanceTextView.setText("거리: " + totalDistance + "km");

        // 난이도 Spinner 설정
        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.difficulty_levels,
                android.R.layout.simple_spinner_item
        );
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);

        // 공개 여부 Spinner 설정
        ArrayAdapter<CharSequence> visibilityAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.visibility_options,
                android.R.layout.simple_spinner_item
        );
        visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visibilitySpinner.setAdapter(visibilityAdapter);

        // 업로드 버튼 클릭 이벤트
        btnUpload.setOnClickListener(v -> uploadCourse());
    }

    private void uploadCourse() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // LatLng 리스트를 Firestore에서 지원하는 Map 형식으로 변환
        List<Map<String, Double>> routePointsData = new ArrayList<>();
        for (LatLng point : routePoints) {
            Map<String, Double> pointData = new HashMap<>();
            pointData.put("latitude", point.latitude);
            pointData.put("longitude", point.longitude);
            routePointsData.add(pointData);
        }

        // 업로드할 데이터 생성
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("name", nameTextView.getText().toString()); // 코스 이름
        courseData.put("distance", totalDistance); // 총 이동거리
        courseData.put("routePoints", routePointsData); // 경로 좌표 리스트
        courseData.put("difficulty", ((Spinner) findViewById(R.id.spinner_difficulty)).getSelectedItem().toString()); // 난이도
        courseData.put("visibility", ((Spinner) findViewById(R.id.spinner_visibility)).getSelectedItem().toString()); // 공개 여부

        // 고유 Document ID 생성
        String documentId = db.collection("courses").document().getId();
        courseData.put("documentId", documentId); // documentId를 데이터 필드로 추가

        // Firestore에 데이터 저장
        db.collection("courses").document(documentId)
                .set(courseData) // 지정된 documentId로 데이터 업로드
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Course uploaded successfully with ID: " + documentId, Toast.LENGTH_SHORT).show();
                    finish(); // 액티비티 종료
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload course: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
