package com.example.ezpos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ezpos.database.JsonUtils;
import com.example.ezpos.database.Usuario;
import com.example.ezpos.IntroHelper;

import java.util.List;

/**
 * Pantalla inicial de login. Comprueba si hay una sesión guardada y, en caso
 * afirmativo, pasa directamente a {@link HomeActivity}. En caso contrario
 * muestra el formulario de acceso.
 */
public class MainActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword, etAsociacion;

    /**
     * Inicializa la pantalla de login. Si detecta una sesión activa redirige al
     * usuario al menú principal, en caso contrario prepara los campos y
     * listeners para iniciar sesión o registrarse.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Este es el layout del login

        // Comprobamos si ya hay una sesión activa
        Usuario sesion = JsonUtils.cargarSesion(this);
        if (sesion != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
            return;
        }

        // Referencias UI
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        etAsociacion = findViewById(R.id.etAsociacion);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvIrRegistro = findViewById(R.id.tvIrRegistro);

        IntroHelper.showIntro(this, "login", getString(R.string.intro_login));

        // Botón de login: valida credenciales localmente guardadas en JSON
        btnLogin.setOnClickListener(v -> {
            String usuario = etUsuario.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String asociacion = etAsociacion.getText().toString().trim();

            if (usuario.isEmpty() || password.isEmpty() || asociacion.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Usuario> listaUsuarios = JsonUtils.cargarUsuarios(this);
            for (Usuario u : listaUsuarios) {
                if (u.nombreUsuario.equals(usuario)
                        && u.contraseña.equals(password)
                        && u.nombreAsociacion.equals(asociacion)) {
                    // Login correcto
                    JsonUtils.guardarSesion(this, u);
                    Toast.makeText(this, "Sesión iniciada", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                    return;
                }
            }

            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        });

        // Link a registro: permite crear un nuevo usuario
        tvIrRegistro.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegistroActivity.class));
        });
    }
}
