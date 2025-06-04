package com.example.ezpos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EZPOSSQLiteHelper extends SQLiteOpenHelper {

    public EZPOSSQLiteHelper(Context context, String nombreDB) {
        super(context, nombreDB, null, 8);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "cantidad INTEGER NOT NULL," +
                "precio REAL NOT NULL," +
                "descripcion TEXT," +
                "imagen TEXT);");

        db.execSQL("CREATE TABLE pedidos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre_cliente TEXT," +
                "fecha_hora TEXT," +
                "total REAL," +
                "pagado REAL," +
                "devolver REAL," +
                "cambio_devuelto INTEGER DEFAULT 0," +
                "entregado INTEGER DEFAULT 0);");

        db.execSQL("CREATE TABLE IF NOT EXISTS detalle_pedido (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_pedido INTEGER NOT NULL, " +
                "id_producto INTEGER NOT NULL, " +
                "cantidad INTEGER NOT NULL, " +
                "precio_unitario REAL NOT NULL, " +
                "FOREIGN KEY(id_pedido) REFERENCES pedidos(id), " +
                "FOREIGN KEY(id_producto) REFERENCES productos(id));");

        db.execSQL("CREATE TABLE historial (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fecha TEXT," +
                "nombre_cliente TEXT," +
                "total REAL," +
                "pagado REAL," +
                "devolver REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS productos;");
        db.execSQL("DROP TABLE IF EXISTS pedidos;");
        db.execSQL("DROP TABLE IF EXISTS detalle_pedido;");
        db.execSQL("DROP TABLE IF EXISTS historial;");
        onCreate(db);
    }
}
