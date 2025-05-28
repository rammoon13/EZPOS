package com.example.ezpos.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Producto {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public double precio;
    public int cantidadDisponible;
    public String imagenUri; // opcional, puede estar vacío
}
