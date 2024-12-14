package com.example.trailrunner.ui.home_like;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        db = FirebaseFirestore.getInstance();

        courseDocumentId = getIntent().getStringExtra("TRACK_DOCUMENT_ID");

        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        reviewInput = findViewById(R.id.reviewInput);
        ratingBar = findViewById(R.id.ratingBar);
        submitReviewButton = findViewById(R.id.submitReviewButton);
        startButton = findViewById(R.id.button);


        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewList);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(reviewAdapter);

        if (getIntent().getBooleanExtra("MY_REVIEWS", false)) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            db.collection("reviews")
                    .whereEqualTo("userName", userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        reviewList.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Review review = doc.toObject(Review.class);
                            if (review != null) {
                                reviewList.add(review);
                            }
                        }
                        reviewAdapter.notifyDataSetChanged();
                    });
        }

        if (courseDocumentId != null) {
            fetchTrackData(courseDocumentId);
            fetchReviewsForCourse(courseDocumentId);
        } else {
            showError("Invalid course ID.");
        }

        submitReviewButton.setOnClickListener(v -> submitReview());
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, NavigationActivity.class);
                intent.putExtra("COURSE_ID", courseDocumentId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchTrackData(String documentId) {
        db.collection("courses")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        courseName = documentSnapshot.getString("name");
                        distance = documentSnapshot.getDouble("distance");
                        difficulty = documentSnapshot.getString("difficulty");

                        updateUI();
                    } else {
                        showError("Course not found.");
                    }
                })
                .addOnFailureListener(e -> showError("Failed to fetch course data."));
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
