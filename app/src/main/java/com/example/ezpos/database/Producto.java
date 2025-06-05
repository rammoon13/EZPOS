/**
 * POJO que almacena la información de un producto del inventario.
 */
package com.example.ezpos.database;

public class Producto {
    private int id;
    private String nombre;
    private int cantidad;
    private double precio;
    private String descripcion;
    private String imagen;

    public Producto(int id, String nombre, int cantidad, double precio, String descripcion, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    public Producto(String nombre, int cantidad, double precio, String descripcion, String imagen) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}
