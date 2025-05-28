package com.example.ezpos.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ezpos.model.LineaPedido;

import java.util.List;

@Dao
public interface LineaPedidoDao {

    @Insert
    void insertarLinea(LineaPedido linea);

    @Query("SELECT * FROM LineaPedido WHERE idPedido = :pedidoId")
    List<LineaPedido> obtenerLineasPorPedido(int pedidoId);
}
