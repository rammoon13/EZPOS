package com.example.ezpos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EZPOSSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ezpos.db";
    private static final int DATABASE_VERSION = 5; // Aumenta si ya existe una base antigua

    public EZPOSSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "cantidad INTEGER NOT NULL," +
                "precio REAL NOT NULL," +
                "descripcion TEXT" +
                ");");

        db.execSQL("CREATE TABLE pedidos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre_cliente TEXT," +
                "fecha_hora TEXT," +
                "total REAL," +
                "pagado REAL," +
                "devolver REAL" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS detalle_pedido (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_pedido INTEGER NOT NULL, " +
                "id_producto INTEGER NOT NULL, " +
                "cantidad INTEGER NOT NULL, " +
                "precio_unitario REAL NOT NULL, " +
                "FOREIGN KEY(id_pedido) REFERENCES pedidos(id), " +
                "FOREIGN KEY(id_producto) REFERENCES productos(id))");

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
        db.execSQL("DROP TABLE IF EXISTS productos;");
        db.execSQL("DROP TABLE IF EXISTS pedidos;");
        db.execSQL("DROP TABLE IF EXISTS pedido_productos");
        db.execSQL("DROP TABLE IF EXISTS historial;");
        onCreate(db);
    }
}
