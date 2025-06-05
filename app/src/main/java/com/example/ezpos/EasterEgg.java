package com.example.ezpos;

import android.content.Context;
import android.widget.Toast;

/**
 * Pequeño guiño que muestra un mensaje oculto al mantener pulsado
 * la pantalla principal.
 */
public class EasterEgg {
    private static final String AUTHOR_MESSAGE = "Desarrollado por Ramon Herrera";

    public static void show(Context context) {
        // Simple Toast con el nombre del autor
        Toast.makeText(context, AUTHOR_MESSAGE, Toast.LENGTH_LONG).show();
    }
}
