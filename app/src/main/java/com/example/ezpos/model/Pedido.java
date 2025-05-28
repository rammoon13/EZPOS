package com.example.ezpos.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pedido {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String fecha;
    public double total;
    public double importeRecibido;
    public double cambioDevuelto;
}
