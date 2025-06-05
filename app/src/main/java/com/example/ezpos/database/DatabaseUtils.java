/**
 * Utilidades para obtener el nombre de la base de datos de cada usuario
 * y crear el helper correspondiente.
 */
// En una clase utilitaria como DatabaseUtils.java (nueva)
package com.example.ezpos.database;

import android.content.Context;

public class DatabaseUtils {

    public static String getNombreBaseDatos(Context context) {
        // Nombre único por usuario y asociación para mantener bases separadas
        Usuario usuario = JsonUtils.cargarSesion(context);
        if (usuario == null) return "ezpos_default.db";

        // Ejemplo: ezpos_ramon_castilloluna.db
        String base = "ezpos_" + usuario.nombreUsuario + "_" + usuario.nombreAsociacion;
        return base.toLowerCase().replaceAll("\\s+", "") + ".db";
    }

    public static EZPOSSQLiteHelper getDatabaseHelper(Context context) {
        // Devuelve un helper configurado con el nombre calculado
        String nombreDB = getNombreBaseDatos(context);
        return new EZPOSSQLiteHelper(context, nombreDB);
    }
}
