package com.hcmut.admin.bktrafficsystem.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.hcmut.admin.bktrafficsystem.model.user.User;
import com.google.gson.Gson;

public class SharedPrefUtils {
    private static final String PREFS_KEY = "Prefs";
    private static final String USER = "USER";
    private static final String RATING = "RATING";
    private static final String NOTI_TOKEN = "NOTI_TOKEN";

    static public void saveUser(Context context, User user) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        pref.edit().putString(USER, gson.toJson(user)).apply();
    }

    static public User getUser(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        return gson.fromJson(pref.getString(USER, null), User.class);
    }

    static public void saveRatingMode(Context context, boolean isCheck) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        pref.edit().putBoolean(RATING, isCheck).apply();
    }

    static public boolean getRatingMode(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(RATING, false);
    }

    static public void saveNotiToken(Context context, String token) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        pref.edit().putString(NOTI_TOKEN, token).apply();
    }

    static public String getNotiToken(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        return pref.getString(NOTI_TOKEN, null);
    }
}
