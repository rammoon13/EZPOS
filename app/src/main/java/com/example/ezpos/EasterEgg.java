package com.example.ezpos;

import android.content.Context;
import android.widget.Toast;

public class EasterEgg {
    private static final String AUTHOR_MESSAGE = "Desarrollado por Ramon Herrera";

    public static void show(Context context) {
        Toast.makeText(context, AUTHOR_MESSAGE, Toast.LENGTH_LONG).show();
    }
}
