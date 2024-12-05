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
import androidx.lifecycle.ViewModelProvider;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    RecyclerView recyclerView;
    TrackChoice adapter;
    public static boolean isCurrentFragment = true;

    @Override
    public void onResume() {
        super.onResume();
        // 즐겨찾기 목록을 최신 상태로 갱신
        ArrayList<Track> updatedFavorites = FavoriteUtils.loadFavorites(getContext());

        // TrackChoice의 인스턴스를 통해 updateFavoriteItems() 호출
        if (adapter != null) {
            adapter.updateFavoriteItems(updatedFavorites);
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        isCurrentFragment = false;
    }


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
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        behavior.setPeekHeight(400); // 초기에 보여질 높이
        behavior.setMaxHeight(ViewGroup.LayoutParams.MATCH_PARENT); // 최대 높이

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = view.findViewById(R.id.homerecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        TrackChoice adapter = new TrackChoice(getContext());  // getContext() 사용
        recyclerView.setAdapter(adapter);
        adapter.setFragment(this);
        getAllTracks(adapter);
        recyclerView.setAdapter(adapter);

        adapter = new TrackChoice(getContext());
        getAllTracks(adapter);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // 서울 위치
        LatLng seoul = new LatLng(37.5665, 126.9780);
        mMap.addMarker(new MarkerOptions().position(seoul).title("Seoul"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15));
    }

    private void getAllTracks(TrackChoice adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("courses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Track 객체 리스트를 저장할 리스트
                        List<Track> trackList = new ArrayList<>();

                        // FavoriteUtils에서 즐겨찾기 목록 불러오기
                        ArrayList<Track> favoriteItems = FavoriteUtils.loadFavorites(getContext());

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                Map<String, Object> courseData = documentSnapshot.getData();

                                if (courseData != null) {
                                    Track track = new Track(courseData);

                                    // 즐겨찾기 목록에 있는 항목이면 isFavorite를 true로 설정
                                    if (favoriteItems.contains(track)) {
                                        track.setFavorite(true);
                                    }

                                    adapter.addItem(track);
                                    trackList.add(track);
                                }
                            }
                        }

                        // 결과 확인
                        for (Track track : trackList) {
                            Log.d("Track Info", track.toString());
                        }
                    } else {
                        Log.d("Firestore Debug", "No documents found in 'courses' collection.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore Debug", "Error fetching data: " + e.getMessage());
                });
    }
}

