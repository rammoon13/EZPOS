/**
 * Datos básicos de los usuarios que pueden iniciar sesión en la app.
 */
package com.example.ezpos.database;

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
