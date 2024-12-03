package com.example.trailrunner.ui.home_like;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trailrunner.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    RecyclerView recyclerView;
    TrackChoice adapter;
    TextView count;

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String param1 = getArguments().getString("param1");
            String param2 = getArguments().getString("param2");
            // 전달받은 파라미터 처리
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.d("MAP", "Map Fragment is null");
        }

        View bottomSheet = view.findViewById(R.id.bottomSheet);
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = view.findViewById(R.id.homerecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TrackChoice();

        adapter.addItem(new Track(R.drawable.mt, "북한산 백운대", "5", "easy"));
        adapter.addItem(new Track(R.drawable.mt, "관악산 메인코스", "10", "normal"));
        adapter.addItem(new Track(R.drawable.mt, "수락산 전망대", "8", "hard"));
        adapter.addItem(new Track(R.drawable.mt, "북한산 백운대", "5", "easy"));
        adapter.addItem(new Track(R.drawable.mt, "관악산 메인코스", "10", "normal"));
        adapter.addItem(new Track(R.drawable.mt, "수락산 전망대", "8", "hard"));
        adapter.addItem(new Track(R.drawable.mt, "북한산 백운대", "5", "easy"));
        adapter.addItem(new Track(R.drawable.mt, "관악산 메인코스", "10", "normal"));
        adapter.addItem(new Track(R.drawable.mt, "수락산 전망대", "8", "hard"));
        recyclerView.setAdapter(adapter);


        /*count = view.findViewById(R.id.textView5);
        count.setText("즐겨찾기 " + adapter.getItemCount() + "개");

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((holder, itemView, position) -> {
            Track item = adapter.getItem(position);
            Toast.makeText(getContext(), "코스 선택됨: " + item.getMountain(),
                    Toast.LENGTH_LONG).show();
        });

        EditText editText = view.findViewById(R.id.editTextText);
        EditText editText2 = view.findViewById(R.id.editTextText2);
        EditText editText3 = view.findViewById(R.id.editTextText3);

        Button button = view.findViewById(R.id.button2);
        button.setOnClickListener(v -> {
            String mountain = editText.getText().toString();
            String distance = editText2.getText().toString();
            String level = editText3.getText().toString();

            adapter.addItem(new Track(R.drawable.mt, mountain, distance, level));
            adapter.notifyDataSetChanged();
            updateCountText();
        });

        adapter.setOnItemRemovedListener(mountain -> {
            Toast.makeText(getContext(), mountain + ": 즐겨찾기에서 제거되었습니다", Toast.LENGTH_SHORT).show();
            updateCountText();
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateCountText();
            }
        });*/
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // 서울 위치
        LatLng seoul = new LatLng(37.5665, 126.9780);
        mMap.addMarker(new MarkerOptions().position(seoul).title("Seoul"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15));
    }

    /*private void updateCountText() {
        count.setText("즐겨찾기 " + adapter.getItemCount() + "개");
    }*/
}
