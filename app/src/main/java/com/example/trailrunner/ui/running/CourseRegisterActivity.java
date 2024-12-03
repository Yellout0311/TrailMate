package com.example.trailrunner.ui.running;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.trailrunner.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

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
        // 업로드 로직 구현
        finish(); // 업로드 완료 후 액티비티 종료
    }
}