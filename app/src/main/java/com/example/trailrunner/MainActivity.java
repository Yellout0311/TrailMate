package com.example.trailrunner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.example.trailrunner.databinding.ActivityMainBinding;
import com.example.trailrunner.ui.home.HomeFragment;
import com.example.trailrunner.ui.like.LikeFragment;
import com.example.trailrunner.ui.profile.ProfileFragment;
import com.example.trailrunner.ui.running.RunningActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private BottomNavigationView bottomNavigationView;

    // test

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.fragment_home) {
                transferTo(new HomeFragment());
                return true;
            } else if (itemId == R.id.fragment_like) {
                transferTo(new LikeFragment());
                return true;
            } else if (itemId == R.id.fragment_running) {
                startActivity(new Intent(this, RunningActivity.class));
                return true;
            } else if (itemId == R.id.fragment_profile) {
                transferTo(new ProfileFragment());
                return true;
            }
            return false;
        });
        // 초기 프래그먼트 설정
        transferTo(new HomeFragment());
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
