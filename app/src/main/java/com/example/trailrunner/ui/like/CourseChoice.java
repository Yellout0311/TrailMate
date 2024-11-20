package com.example.trailrunner.ui.like;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import android.os.Handler;

import com.example.trailrunner.R;

public class CourseChoice extends RecyclerView.Adapter<CourseChoice.ViewHolder>
        implements OnPersonItemClickListener {
    ArrayList<Course> items = new ArrayList<>();
    OnPersonItemClickListener listener;
    OnItemRemovedListener removedListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.fragment_course_choice, viewGroup, false);

        return new ViewHolder(itemView, this);  // adapter 인스턴스를 전달
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Course item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Course item) {
        items.add(item);
    }

    public void setItems(ArrayList<Course> items) {
        this.items = items;
    }

    public Course getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Course item) {
        items.set(position, item);
    }

    public interface OnItemRemovedListener {
        void onItemRemoved(String mountain);
    }

    public void setOnItemRemovedListener(OnItemRemovedListener removedListener) {
        this.removedListener = removedListener;
    }

    public void removeItem(int position) {
        if (position != RecyclerView.NO_POSITION) {
            Course removedItem = items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());

            if (removedListener != null) {
                removedListener.onItemRemoved(removedItem.getMountain());
            }
        }
    }

    public void setOnItemClickListener(OnPersonItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if(listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView textView2;
        TextView textView3;
        ImageButton imageButton;
        CourseChoice adapter;  // adapter 필드를 추가

        public ViewHolder(View itemView, final CourseChoice adapter) {
            super(itemView);
            this.adapter = adapter;  // 전달받은 adapter를 필드에 저장

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
            imageButton = itemView.findViewById(R.id.imageButton);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if(adapter.listener != null && position != RecyclerView.NO_POSITION) {
                    adapter.listener.onItemClick(this, view, position);
                }
            });

            imageButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    new Handler().postDelayed(() -> adapter.removeItem(position), 500);  // 2초 딜레이 후 삭제
                }
            });
        }

        public void setItem(Course item) {
            imageView.setImageResource(item.getImageResourceId());
            textView.setText(item.getMountain());
            textView2.setText(item.getDistance());
            textView3.setText(item.getLevel());
        }
    }
}
