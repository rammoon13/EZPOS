package com.example.ezpos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EZPOSSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ezpos.db";
    private static final int DATABASE_VERSION = 2;

    public EZPOSSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Aquí creamos las tablas
        db.execSQL("CREATE TABLE productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "cantidad INTEGER NOT NULL," +
                "precio REAL NOT NULL" +
                ");");

        db.execSQL("CREATE TABLE pedidos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre_cliente TEXT," +
                "total REAL" +
                ");");

        db.execSQL("CREATE TABLE historial (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fecha TEXT," +
                "nombre_cliente TEXT," +
                "total REAL," +
                "pagado REAL," +
                "devolver REAL" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si cambias algo, eliminas y creas de nuevo
        db.execSQL("DROP TABLE IF EXISTS productos;");
        db.execSQL("DROP TABLE IF EXISTS pedidos;");
        db.execSQL("DROP TABLE IF EXISTS historial;");
        onCreate(db);
    }
}

