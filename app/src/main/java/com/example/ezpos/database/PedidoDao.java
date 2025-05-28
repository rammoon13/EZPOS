package com.example.ezpos.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ezpos.model.Pedido;

import java.util.List;

@Dao
public interface PedidoDao {

    @Insert
    long insertarPedido(Pedido pedido); // devolvemos el id generado

    @Query("SELECT * FROM Pedido ORDER BY fecha DESC")
    List<Pedido> obtenerTodos();
}
