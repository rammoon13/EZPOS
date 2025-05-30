package com.example.ezpos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RegistroActivity extends AppCompatActivity {

    private EditText etEmail, etNombreCompleto, etNombreUsuario, etAsociacion, etPassword, etConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Referencias UI
        etEmail = findViewById(R.id.etEmail);
        etNombreCompleto = findViewById(R.id.etNombreCompleto);
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etAsociacion = findViewById(R.id.etAsociacion);
        etPassword = findViewById(R.id.etPassword);
        etConfirmar = findViewById(R.id.etConfirmarPassword);

        Button btnRegistro = findViewById(R.id.btnRegistrarse);
        TextView tvIrLogin = findViewById(R.id.tvIrLogin);

        btnRegistro.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String nombreCompleto = etNombreCompleto.getText().toString().trim();
            String usuario = etNombreUsuario.getText().toString().trim();
            String asociacion = etAsociacion.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String confirmar = etConfirmar.getText().toString().trim();

            if (email.isEmpty() || nombreCompleto.isEmpty() || usuario.isEmpty()
                    || asociacion.isEmpty() || pass.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmar)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Usuario> usuarios = JsonUtils.cargarUsuarios(this);
            for (Usuario u : usuarios) {
                if (u.nombreUsuario.equals(usuario) && u.nombreAsociacion.equals(asociacion)) {
                    Toast.makeText(this, "Ese usuario ya existe", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Usuario nuevo = new Usuario(email, nombreCompleto, usuario, asociacion, pass);
            usuarios.add(nuevo);
            JsonUtils.guardarUsuarios(this, usuarios);

            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegistroActivity.this, MainActivity.class));
            finish();
        });

        tvIrLogin.setOnClickListener(v -> finish()); // Volver al login
    }
}
