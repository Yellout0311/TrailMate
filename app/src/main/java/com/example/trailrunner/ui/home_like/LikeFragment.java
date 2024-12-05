package com.example.trailrunner.ui.home_like;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LikeFragment extends Fragment {
    RecyclerView recyclerView;
    TrackChoice adapter;
    TextView count;
    public static boolean isCurrentFragment = false;
    ArrayList<Track> favoriteItems;

    @Override
    public void onResume() {
        super.onResume();
        isCurrentFragment = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isCurrentFragment = false;
    }

    public static LikeFragment newInstance(String param1, String param2) {
        LikeFragment fragment = new LikeFragment();
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
        return inflater.inflate(R.layout.fragment_like, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = view.findViewById(R.id.homerecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TrackChoice(getContext());  // getContext() 사용
        recyclerView.setAdapter(adapter);
        adapter.setFragment(this);
        getAllTracks(adapter);
        recyclerView.setAdapter(adapter);

        adapter = new TrackChoice(getContext());
        getAllTracks(adapter);
        // LikeFragment에서 TrackChoice 어댑터의 updateFavoriteItems 호출
        //adapter.updateFavoriteItems(favoriteItems); // 새로 업데이트된 즐겨찾기 아이템들

        count = view.findViewById(R.id.textView5);
        count.setText("즐겨찾기 " + adapter.getItemCount() + "개");

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((holder, itemView, position) -> {
            Track item = adapter.getItem(position);
            Toast.makeText(getContext(), "코스 선택됨: " + item.getCourseName(),
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
        });
    }

    private void updateCountText() {
        count.setText("즐겨찾기 " + adapter.getItemCount() + "개");
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

                                    // 즐겨찾기 목록에 있는 항목만 추가
                                    if (track.isFavorite()) {
                                        adapter.addItem(track);
                                        trackList.add(track);
                                    }
                                }
                            }
                        }

                        // 결과 확인
                        for (Track track : trackList) {
                            Log.d("Track Info", track.toString());
                        }

                        // 어댑터에 새로운 즐겨찾기 리스트를 갱신
                        adapter.updateFavoriteItems(trackList);
                    } else {
                        Log.d("Firestore Debug", "No documents found in 'courses' collection.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore Debug", "Error fetching data: " + e.getMessage());
                });
    }


}

