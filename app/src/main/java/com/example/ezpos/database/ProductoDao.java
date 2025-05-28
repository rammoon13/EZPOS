package com.example.ezpos.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ezpos.model.Producto;

import java.util.List;

@Dao
public interface ProductoDao {

    @Insert
    void insertarProducto(Producto producto);

    @Update
    void actualizarProducto(Producto producto);

    @Delete
    void eliminarProducto(Producto producto);

    @Query("SELECT * FROM Producto ORDER BY nombre ASC")
    List<Producto> obtenerTodos();

    @Query("SELECT * FROM Producto WHERE id = :id")
    Producto obtenerPorId(int id);
}
