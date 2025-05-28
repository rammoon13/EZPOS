    package com.example.ezpos;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    public class LoginActivity extends AppCompatActivity {

        private EditText etUsuario, etContrasena, etAsociacion;
        private Button btnIniciarSesion;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            etUsuario = findViewById(R.id.etUsuario);
            etContrasena = findViewById(R.id.etContrasena);
            etAsociacion = findViewById(R.id.etAsociacion);
            btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

            btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String usuario = etUsuario.getText().toString().trim();
                    String contrasena = etContrasena.getText().toString().trim();
                    String asociacion = etAsociacion.getText().toString().trim();

                    if (usuario.isEmpty() || contrasena.isEmpty() || asociacion.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                    } else {
                        // Aquí puedes hacer una validación real si tienes usuarios
                        // Por ahora asumimos que todo va bien
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }
