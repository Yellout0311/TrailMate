package com.example.trailrunner.ui.home_like;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trailrunner.R;
import com.example.trailrunner.ui.review.Review;
import com.example.trailrunner.ui.review.ReviewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String courseName;
    private Double distance;
    private String difficulty;
    private String courseDocumentId;

    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;

    private EditText reviewInput;
    private RatingBar ratingBar;
    private Button submitReviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        db = FirebaseFirestore.getInstance();

        int position = getIntent().getIntExtra("TRACK_POSITION", 0);

        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        reviewInput = findViewById(R.id.reviewInput);
        ratingBar = findViewById(R.id.ratingBar);
        submitReviewButton = findViewById(R.id.submitReviewButton);

        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewList);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(reviewAdapter);

        submitReviewButton.setOnClickListener(v -> submitReview());

        fetchTrackData(position);
    }

    private void fetchTrackData(int position) {
        db.collection("courses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                        if (position < documents.size()) {
                            DocumentSnapshot document = documents.get(position);

                            courseName = document.getString("name");
                            distance = document.getDouble("distance");
                            difficulty = document.getString("difficulty");

                            updateUI();

                            // 선택된 코스의 도큐먼트 ID를 가져와서 리뷰를 불러옴
                            courseDocumentId = document.getId();
                            fetchReviewsForCourse(courseDocumentId);
                        } else {
                            showError("Invalid position.");
                        }
                    } else {
                        showError("No courses available.");
                    }
                })
                .addOnFailureListener(e -> showError("Failed to fetch courses."));
    }

    private void fetchReviewsForCourse(String courseDocumentId) {
        db.collection("reviews")
                .whereEqualTo("courseDocumentId", courseDocumentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    reviewList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Review review = document.toObject(Review.class);
                            if (review != null) {
                                reviewList.add(review);
                            }
                        }
                        reviewAdapter.notifyDataSetChanged();
                    } else {
                        showError("No reviews found for this course.");
                    }
                })
                .addOnFailureListener(e -> showError("Failed to fetch reviews: " + e.getMessage()));
    }


    private void submitReview() {
        String reviewText = reviewInput.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (reviewText.isEmpty()) {
            showError("Please write a review.");
            return;
        }

        String userName = "Anonymous"; // 기본값 설정

        // 현재 사용자 정보가 존재하는지 확인
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // displayName이 설정되어 있으면 사용, 아니면 email 사용
            userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            if (userName == null) {
                userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            }
        }

        if (courseDocumentId == null) {
            showError("Course ID is missing.");
            return;
        }

        Review review = new Review(userName, rating, reviewText, System.currentTimeMillis(), courseDocumentId);
        db.collection("reviews")
                .add(review)
                .addOnSuccessListener(documentReference -> {
                    showError("Review submitted successfully.");
                    reviewInput.setText("");
                    ratingBar.setRating(0);
                    fetchReviewsForCourse(courseDocumentId); // 리뷰가 제출되었으므로 특정 코스의 리뷰를 다시 가져옴
                })
                .addOnFailureListener(e -> showError("Failed to submit review."));
    }

    private void updateUI() {
        TextView courseNameTextView = findViewById(R.id.courseName);
        TextView distanceTextView = findViewById(R.id.distance);
        TextView difficultyTextView = findViewById(R.id.difficulty);
        ImageView imageView = findViewById(R.id.imageView);

        courseNameTextView.setText(courseName);
        String distanceText = distance != null ? distance + " km" : "N/A";
        distanceTextView.setText(distanceText);
        difficultyTextView.setText(difficulty);
        imageView.setImageResource(R.drawable.mt);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
