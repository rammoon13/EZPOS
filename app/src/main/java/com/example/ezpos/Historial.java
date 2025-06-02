package com.example.ezpos;

public class Historial {
    private int id;
    private String fecha;
    private String nombreCliente;
    private double total;
    private double pagado;
    private double devolver;

    public Historial(int id, String fecha, String nombreCliente, double total, double pagado, double devolver) {
        this.id = id;
        this.fecha = fecha;
        this.nombreCliente = nombreCliente;
        this.total = total;
        this.pagado = pagado;
        this.devolver = devolver;
    }

    public Historial(String fecha, String nombreCliente, double total, double pagado, double devolver) {
        this.fecha = fecha;
        this.nombreCliente = nombreCliente;
        this.total = total;
        this.pagado = pagado;
        this.devolver = devolver;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public double getPagado() { return pagado; }
    public void setPagado(double pagado) { this.pagado = pagado; }

    public double getDevolver() { return devolver; }
    public void setDevolver(double devolver) { this.devolver = devolver; }
}
