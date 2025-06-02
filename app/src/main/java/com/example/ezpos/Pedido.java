package com.example.ezpos;

public class Pedido {
    private int id;
    private String nombreCliente;
    private double total;

    public Pedido(int id, String nombreCliente, double total) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.total = total;
    }

    public Pedido(String nombreCliente, double total) {
        this.nombreCliente = nombreCliente;
        this.total = total;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}

