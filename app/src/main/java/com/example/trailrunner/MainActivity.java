package com.example.trailrunner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.trailrunner.databinding.ActivityMainBinding;
import com.example.trailrunner.ui.home.HomeFragment;
import com.example.trailrunner.ui.like.LikeFragment;
import com.example.trailrunner.ui.profile.ProfileFragment;
import com.example.trailrunner.ui.running.RunningActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        bottomNavigationView.setOnItemReselectedListener(item -> {

        });

        // 초기 프래그먼트 설정
        transferTo(HomeFragment.newInstance("param1", "param2"));
    }
    private void transferTo(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void openRunningActivity() {
        Intent intent = new Intent(this, RunningActivity.class);
        startActivity(intent);
    }
}
