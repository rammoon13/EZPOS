package com.example.ezpos;

import java.util.List;

public class Pedido {
    private int id;
    private String nombreCliente;
    private String fechaHora;
    private List<Producto> productos;
    private double total;
    private double pagado;
    private double aDevolver;

    public Pedido(int id, String nombreCliente, String fechaHora,
                  List<Producto> productos, double total, double pagado, double aDevolver) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.fechaHora = fechaHora;
        this.productos = productos;
        this.total = total;
        this.pagado = pagado;
        this.aDevolver = aDevolver;
    }

    public int getId() { return id; }
    public String getNombreCliente() { return nombreCliente; }
    public String getFechaHora() { return fechaHora; }
    public List<Producto> getProductos() { return productos; }
    public double getTotal() { return total; }
    public double getPagado() { return pagado; }
    public double getADevolver() { return aDevolver; }

    public void setId(int id) { this.id = id; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
    public void setTotal(double total) { this.total = total; }
    public void setPagado(double pagado) { this.pagado = pagado; }
    public void setADevolver(double aDevolver) { this.aDevolver = aDevolver; }
}
