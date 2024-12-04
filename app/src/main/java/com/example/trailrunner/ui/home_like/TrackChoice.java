package com.example.trailrunner.ui.home_like;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trailrunner.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class TrackChoice extends RecyclerView.Adapter<TrackChoice.ViewHolder>
        implements OnPersonItemClickListener {

    private final ArrayList<Track> items = new ArrayList<>();
    private OnPersonItemClickListener listener;
    private OnItemRemovedListener removedListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.fragment_course_choice, viewGroup, false);
        return new ViewHolder(itemView);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;
        private final TextView textView2;
        private final TextView textView3;
        private final ImageButton imageButton;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
            imageButton = itemView.findViewById(R.id.imageButton);

            // 아이템 클릭 이벤트
            itemView.setOnClickListener(view -> {
            });

            // 삭제 버튼 이벤트
            imageButton.setOnClickListener(v -> {
            });
        }

        public void setItem(Track item) {
            textView.setText(item.getMountain());
            textView2.setText(item.getDistance());
            textView3.setText(item.getDifficulty());
            imageView.setImageResource(R.drawable.mt); // 산 이미지로 설정
        }
    }
}
