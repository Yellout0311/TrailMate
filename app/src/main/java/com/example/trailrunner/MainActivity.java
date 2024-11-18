package com.example.trailrunner;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.trailrunner.ui.home.HomeFragment;
import com.example.trailrunner.ui.like.LikeFragment;
import com.example.trailrunner.ui.profile.ProfileFragment;
import com.example.trailrunner.ui.running.RunningActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // 로그인되지 않은 상태라면 EmailPasswordActivity로 이동
            moveToLogin();
            return;
        }

        // 로그인된 상태라면 메인 액티비티의 콘텐츠 설정
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.fragment_home) {
                transferTo(HomeFragment.newInstance("param1", "param2"));
                return true;
            } else if (itemId == R.id.fragment_like) {
                transferTo(LikeFragment.newInstance("param1", "param2"));
                return true;
            } else if (itemId == R.id.fragment_running) {
                startActivity(new Intent(this, RunningActivity.class));
                return true;
            } else if (itemId == R.id.fragment_profile) {
                transferTo(ProfileFragment.newInstance("param1", "param2"));
                return true;
            }
            return false;
        });

        bottomNavigationView.setOnItemReselectedListener(item -> {});

        // 초기 프래그먼트 설정
        transferTo(HomeFragment.newInstance("param1", "param2"));
    }

    private void moveToLogin() {
        Intent intent = new Intent(this, EmailPasswordActivity.class);
        startActivity(intent);
        finish(); // 메인 액티비티 종료
    }

    private void transferTo(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
