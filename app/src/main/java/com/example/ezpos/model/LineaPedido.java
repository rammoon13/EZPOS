package com.example.ezpos.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LineaPedido {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int idPedido;
    public int idProducto;
    public int cantidadVendida;
    public double precioUnitario;
}
