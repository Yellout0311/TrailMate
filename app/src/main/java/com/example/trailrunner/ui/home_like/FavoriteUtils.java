package com.example.trailrunner.ui.home_like;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.trailrunner.ui.home_like.Track;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FavoriteUtils {

    public static void saveFavorites(Context context, ArrayList<Track> favoriteItems) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Track 객체를 JSON 문자열로 변환하여 저장
        Gson gson = new Gson();
        String json = gson.toJson(favoriteItems);
        editor.putString("favorite_tracks", json);
        editor.apply();

        Log.d("FavoriteUtils", "Favorites saved: " + json);
    }

    public static ArrayList<Track> loadFavorites(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("favorite_tracks", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Track>>() {}.getType();
            ArrayList<Track> favoriteItems = gson.fromJson(json, type);
            Log.d("FavoriteUtils", "Favorites loaded: " + favoriteItems.toString());
            return favoriteItems;
        }

        return new ArrayList<>();
    }

    // 즐겨찾기에서 특정 Track을 제거하는 메서드
    public static void removeFavorite(Context context, Track trackToRemove) {
        ArrayList<Track> favoriteItems = loadFavorites(context);

        // 즐겨찾기 리스트에서 해당 Track을 제거
        if (favoriteItems.contains(trackToRemove)) {
            favoriteItems.remove(trackToRemove);
            Log.d("FavoriteUtils", "Removed from favorites: " + trackToRemove.getCourseName());

            // 수정된 리스트를 다시 저장
            saveFavorites(context, favoriteItems);
        }
    }
}
