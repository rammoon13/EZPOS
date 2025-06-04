// En una clase utilitaria como DatabaseUtils.java (nueva)
package com.example.ezpos;

import android.content.Context;

public class DatabaseUtils {

    public static String getNombreBaseDatos(Context context) {
        Usuario usuario = JsonUtils.cargarSesion(context);
        if (usuario == null) return "ezpos_default.db";

        // Ejemplo: ezpos_ramon_castilloluna.db
        String base = "ezpos_" + usuario.nombreUsuario + "_" + usuario.nombreAsociacion;
        return base.toLowerCase().replaceAll("\\s+", "") + ".db";
    }

    public static EZPOSSQLiteHelper getDatabaseHelper(Context context) {
        String nombreDB = getNombreBaseDatos(context);
        return new EZPOSSQLiteHelper(context, nombreDB);
    }
}
