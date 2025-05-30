package com.example.ezpos;

public class Usuario {
    public String email;
    public String nombreCompleto;
    public String nombreUsuario;
    public String nombreAsociacion;
    public String contraseña;

    public Usuario(String email, String nombreCompleto, String nombreUsuario, String nombreAsociacion, String contraseña) {
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.nombreAsociacion = nombreAsociacion;
        this.contraseña = contraseña;
    }
}
