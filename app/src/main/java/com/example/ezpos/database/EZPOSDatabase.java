package com.example.ezpos.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ezpos.model.Producto;
import com.example.ezpos.model.Pedido;
import com.example.ezpos.model.LineaPedido;

@Database(entities = {Producto.class, Pedido.class, LineaPedido.class}, version = 1, exportSchema = false)
public abstract class EZPOSDatabase extends RoomDatabase {

    private static EZPOSDatabase instancia;

    public abstract ProductoDao productoDao();
    public abstract PedidoDao pedidoDao();
    public abstract LineaPedidoDao lineaPedidoDao();

    public static synchronized EZPOSDatabase obtenerInstancia(Context context) {
        if (instancia == null) {
            instancia = Room.databaseBuilder(context.getApplicationContext(),
                            EZPOSDatabase.class, "ezpos_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instancia;
    }
}
