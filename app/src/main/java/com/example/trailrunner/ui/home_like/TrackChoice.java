package com.example.trailrunner.ui.home_like;

import static com.example.trailrunner.ui.home_like.LikeFragment.isCurrentFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trailrunner.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class TrackChoice extends RecyclerView.Adapter<TrackChoice.ViewHolder>
        implements OnPersonItemClickListener {

    private final ArrayList<Track> items = new ArrayList<>();
    private OnPersonItemClickListener listener;
    private OnItemRemovedListener removedListener;
    private static final ArrayList<Track> favoriteItems = new ArrayList<>(); // 즐겨찾기 객체 리스트
    private Fragment currentFragment;  // 현재 프래그먼트 저장



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.fragment_course_choice, viewGroup, false);
        return new ViewHolder(itemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Track item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Track getItem(int position) {
        return items.get(position);
    }

    public void addItem(Track item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void removeItem(int position) {
        if (position != RecyclerView.NO_POSITION) {
            Track removedItem = items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());

            if (removedListener != null) {
                removedListener.onItemRemoved(removedItem.getMountain());
            }
        }
    }

    public void setOnItemRemovedListener(OnItemRemovedListener removedListener) {
        this.removedListener = removedListener;
    }

    public void setOnItemClickListener(OnPersonItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    public interface OnItemRemovedListener {
        void onItemRemoved(String mountain);
    }

    public void addFavorite(Track track) {
        if (track.isFavorite() && !favoriteItems.contains(track)) {
            favoriteItems.add(track);
            Log.d("TrackChoice", "Added to favorites: " + track.getMountain());
        }
    }

    public void removeFavorite(Track track) {
        // isFavorite가 true일 때만 제거
        if (!track.isFavorite()) {
            Log.d("TrackChoice", "Removing from favorites: " + track.getMountain());

            if (favoriteItems.contains(track)) {
                favoriteItems.remove(track);  // 리스트에서 제거
                Log.d("TrackChoice", "Successfully removed: " + track.getMountain());
            } else {
                Log.d("TrackChoice", "Track not found in favorites: " + track.getMountain());
            }

            // 현재 프래그먼트가 LikeFragment일 때만 items에서 제거
            if (isCurrentFragment && !track.isFavorite()) {
                items.remove(track);  // items에서 제거 (선택적으로)
                Log.d("TrackChoice", "Removed from items: " + track.getMountain());
            }

            notifyDataSetChanged();  // RecyclerView 갱신
        }
    }




    public void setTrackList(List<Track> newTrackList) {
        newTrackList = new ArrayList<>(newTrackList);
        notifyDataSetChanged(); // 데이터를 갱신합니다.
    }


    public ArrayList<Track> getFavoriteItems() {
        return new ArrayList<>(favoriteItems); // 복사본 반환
    }

    public void addAll(List<Track> tracks) {
        items.addAll(tracks);
        notifyDataSetChanged();
    }

    public void updateFavoriteItems(List<Track> newFavoriteItems) {
        favoriteItems.clear(); // 기존 리스트를 지운 뒤
        favoriteItems.addAll(newFavoriteItems); // 새로운 아이템 추가
        notifyDataSetChanged(); // 데이터 갱신
    }

    public void setFragment(Fragment fragment) {
        this.currentFragment = fragment;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;
        private final TextView textView2;
        private final TextView textView3;
        private final ImageButton imageButton;
        private final TrackChoice adapter;




        public void setItem(Track item) {
            textView.setText(item.getMountain());
            textView2.setText(item.getDistance());
            textView3.setText(item.getDifficulty());
            imageView.setImageResource(R.drawable.mt); // 산 이미지로 설정

            boolean isFavorite = favoriteItems.contains(item);
            imageButton.setImageResource(
                    item.isFavorite() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
            );
        }

        public ViewHolder(View itemView, TrackChoice adapter) {
            super(itemView);
            this.adapter = adapter;
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
            imageButton = itemView.findViewById(R.id.imageButton);

            // 아이템 클릭 이벤트
            itemView.setOnClickListener(view -> {
            });

            // 즐겨찾기 버튼 클릭 시
            imageButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Track currentTrack = adapter.getItem(position);
                    boolean isFavorite = currentTrack.isFavorite();

                    // 즐겨찾기 상태 토글
                    currentTrack.setFavorite(!isFavorite);

                    if (currentTrack.isFavorite()) {
                        adapter.addFavorite(currentTrack);
                        Log.d("TrackChoice", "Added to favorites: " + currentTrack.getMountain());
                    } else {
                        adapter.removeFavorite(currentTrack); // 제거 메서드 호출
                        Log.d("TrackChoice", "Removed from favorites: " + currentTrack.getMountain());
                    }

                    // UI 업데이트
                    imageButton.setImageResource(
                            currentTrack.isFavorite() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
                    );
                }
            });


        }

    }
}
