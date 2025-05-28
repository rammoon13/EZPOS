package com.example.ezpos.model;

public class Pedido {
    private String cliente;
    private double total;
    private double pagado;
    private double cambio;

    public Pedido(String cliente, double total, double pagado, double cambio) {
        this.cliente = cliente;
        this.total = total;
        this.pagado = pagado;
        this.cambio = cambio;
    }

    // Getters y setters
    public String getCliente() {
        return cliente;
    }

    public double getTotal() {
        return total;
    }

    public double getPagado() {
        return pagado;
    }

    public double getCambio() {
        return cambio;
    }
}
