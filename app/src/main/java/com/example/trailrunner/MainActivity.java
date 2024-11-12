package com.example.trailrunner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.trailrunner.databinding.ActivityMainBinding;
import com.example.trailrunner.ui.running.RunningActivity;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Navigation Controller 설정
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // BottomNavigationView와 NavController를 연결
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        // RunningActivity로 이동 처리
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_running) {
                openRunningActivity();
                return true;
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });
    }

    private void openRunningActivity() {
        Intent intent = new Intent(this, RunningActivity.class);
        startActivity(intent);
    }
}
