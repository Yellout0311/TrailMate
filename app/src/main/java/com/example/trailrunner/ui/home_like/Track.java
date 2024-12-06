package com.example.trailrunner.ui.home_like;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Track {
    private String courseName;
    private double distance;
    private String difficulty;
    private List<LatLng> routePoints;
    private String visibility;
    private boolean isFavorite;
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Track track = (Track) obj;
        return Double.compare(track.distance, distance) == 0 &&
                Objects.equals(courseName, track.courseName) &&
                Objects.equals(difficulty, track.difficulty) &&
                Objects.equals(visibility, track.visibility) &&
                Objects.equals(routePoints, track.routePoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseName, distance, difficulty, routePoints, visibility);
    }


    // Firestore 데이터 기반 생성자
    public Track(Map<String, Object> courseData) {
        try {
            this.isFavorite = isFavorite();
            // 거리
            this.distance = courseData.containsKey("distance") && courseData.get("distance") instanceof Number
                    ? ((Number) courseData.get("distance")).doubleValue()
                    : 0.0;

            // 난이도
            this.difficulty = courseData.containsKey("difficulty") && courseData.get("difficulty") instanceof String
                    ? (String) courseData.get("difficulty")
                    : "N/A";

            // 공개 여부
            this.visibility = courseData.containsKey("visibility") && courseData.get("visibility") instanceof String
                    ? (String) courseData.get("visibility")
                    : "N/A";

            // 경로 데이터 변환
            this.routePoints = new ArrayList<>();
            if (courseData.containsKey("routePoints") && courseData.get("routePoints") instanceof List) {
                List<Map<String, Double>> routePointsData = (List<Map<String, Double>>) courseData.get("routePoints");
                for (Map<String, Double> pointData : routePointsData) {
                    double latitude = pointData.get("latitude");
                    double longitude = pointData.get("longitude");
                    this.routePoints.add(new LatLng(latitude, longitude));
                }
            }

            // 산 이름 (임시 값)
            this.courseName = courseData.containsKey("name") && courseData.get("name") instanceof String
                    ? (String) courseData.get("name")
                    : "Unknown Mountain";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getter & Setter 메서드
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String mountain) {
        this.courseName = mountain;
    }

    public String getDistance() {
        return distance + " km";
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public List<LatLng> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<LatLng> routePoints) {
        this.routePoints = routePoints;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
    public boolean isFavorite() {
        return isFavorite;
    }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // 디버깅용 toString 메서드
    @Override
    public String toString() {
        return "Track{" +
                "mountain='" + courseName + '\'' +
                ", distance=" + distance +
                ", difficulty='" + difficulty + '\'' +
                ", routePoints=" + routePoints +
                ", visibility='" + visibility + '\'' +
                '}';
    }
}
