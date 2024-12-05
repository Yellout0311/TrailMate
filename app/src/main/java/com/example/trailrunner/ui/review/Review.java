package com.example.trailrunner.ui.review;

public class Review {
    private String userName;
    private float rating;
    private String comment;
    private long timestamp;
    private String courseDocumentId;

    // 기본 생성자 필요 (Firestore에서 객체 생성 시 필요)
    public Review() {
    }

    // 생성자
    public Review(String userName, float rating, String comment, long timestamp, String courseDocumentId) {
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
        this.courseDocumentId = courseDocumentId;
    }

    // Getter와 Setter 메서드
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCourseDocumentId() {
        return courseDocumentId;
    }

    public void setCourseDocumentId(String courseDocumentId) {
        this.courseDocumentId = courseDocumentId;
    }
}
