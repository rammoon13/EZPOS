package com.example.ezpos;

import android.database.Cursor;

import java.util.List;

public class Pedido {
    private int id;
    private String nombreCliente;
    private String fechaHora;
    private List<Producto> productos;
    private double total;
    private double pagado;
    private double aDevolver;
    private boolean cambioDevuelto;
    private boolean entregado;

    public Pedido(int id, String nombreCliente, String fechaHora,
                  List<Producto> productos, double total, double pagado,
                  double aDevolver, boolean cambioDevuelto, boolean entregado) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.fechaHora = fechaHora;
        this.productos = productos;
        this.total = total;
        this.pagado = pagado;
        this.aDevolver = aDevolver;
        this.cambioDevuelto = cambioDevuelto;
        this.entregado = entregado;
    }

    public static Pedido fromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        String nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente"));
        String fechaHora = cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora"));
        double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        double pagado = cursor.getDouble(cursor.getColumnIndexOrThrow("pagado"));
        double devolver = cursor.getDouble(cursor.getColumnIndexOrThrow("devolver"));
        boolean cambioDevuelto = cursor.getInt(cursor.getColumnIndexOrThrow("cambio_devuelto")) == 1;
        boolean entregado = cursor.getInt(cursor.getColumnIndexOrThrow("entregado")) == 1;

        return new Pedido(id, nombreCliente, fechaHora, null, total, pagado, devolver, cambioDevuelto, entregado);
    }


    public int getId() { return id; }
    public String getNombreCliente() { return nombreCliente; }
    public String getFechaHora() { return fechaHora; }
    public List<Producto> getProductos() { return productos; }
    public double getTotal() { return total; }
    public double getPagado() { return pagado; }
    public double getADevolver() { return aDevolver; }
    public boolean isCambioDevuelto() { return cambioDevuelto; }
    public boolean isEntregado() { return entregado; }

    public void setId(int id) { this.id = id; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
    public void setTotal(double total) { this.total = total; }
    public void setPagado(double pagado) { this.pagado = pagado; }
    public void setADevolver(double aDevolver) { this.aDevolver = aDevolver; }
    public void setCambioDevuelto(boolean cambioDevuelto) { this.cambioDevuelto = cambioDevuelto; }
    public void setEntregado(boolean entregado) { this.entregado = entregado; }
}
