/**
 * Utilidades para obtener el nombre de la base de datos de cada usuario
 * y crear el helper correspondiente.
 */
// En una clase utilitaria como DatabaseUtils.java (nueva)
package com.example.ezpos.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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

    /**
     * Cierra y elimina la base de datos actual para poder reemplazarla.
     */
    public static void reemplazarBaseDatos(Context context, InputStream origen) throws Exception {
        String nombreDB = getNombreBaseDatos(context);
        File dbFile = context.getDatabasePath(nombreDB);

        // Asegurar que la conexión actual esté cerrada y eliminar el archivo
        EZPOSSQLiteHelper helper = getDatabaseHelper(context);
        helper.close();
        context.deleteDatabase(nombreDB);

        // Copiar el nuevo contenido
        OutputStream out = new FileOutputStream(dbFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = origen.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        origen.close();
        out.close();

        // Ajustar la versión para evitar migraciones que borren datos
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        db.setVersion(EZPOSSQLiteHelper.DB_VERSION);
        db.close();
    }
}
