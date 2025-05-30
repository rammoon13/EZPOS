package com.example.ezpos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AgregarProductoActivity extends AppCompatActivity {

    ImageButton btnImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        // Botón de imagen (por ahora sin funcionalidad real)
        btnImagen = findViewById(R.id.btnSeleccionarImagen);
        btnImagen.setOnClickListener(v -> {
            Toast.makeText(this, "Aquí se abriría la galería o cámara", Toast.LENGTH_SHORT).show();
            // En el futuro se añade: startActivityForResult o registerForActivityResult
        });

        // Botón Atrás
        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());

        // Botón Agregar Producto
        Button btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            Toast.makeText(this, "Producto añadido correctamente", Toast.LENGTH_SHORT).show();
            finish(); // Regresa a InventarioFragment
        });
    }
}
