package com.example.ezpos;

import java.io.Serializable;

public class ProductoPedido implements Serializable {
    public int id;
    public String nombre;
    public double precioUnitario;
    public int cantidad;

    public ProductoPedido(int id, String nombre, double precioUnitario, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }
}
