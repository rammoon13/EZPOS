package com.example.ezpos;

import android.content.Context;
import android.widget.Toast;

/**
 * Simple Easter egg to identify the original author.
 */
public class EasterEgg {
    private static final String AUTHOR_MESSAGE = "Desarrollado por TU NOMBRE";

    public static void show(Context context) {
        Toast.makeText(context, AUTHOR_MESSAGE, Toast.LENGTH_LONG).show();
    }
}
