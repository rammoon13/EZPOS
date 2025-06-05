package com.example.ezpos;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;
/**
 * Muestra un tutorial al usuario la primera vez que entra en una pantalla.
 * Se apoya en SharedPreferences para no repetir la introducción.
 */

public class IntroHelper {
    private static final String PREFS_NAME = "intro_prefs";

    public static void showIntro(Context context, String key, String message) {
        // Muestra un diálogo de ayuda sólo la primera vez que se llama con esa clave
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
