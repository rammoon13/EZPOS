package com.example.ezpos;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

public class IntroHelper {
    private static final String PREFS_NAME = "intro_prefs";

    public static void showIntro(Context context, String key, String message) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(key, true)) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.tutorial_title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            prefs.edit().putBoolean(key, false).apply();
        }
    }
}
