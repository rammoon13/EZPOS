package com.example.ezpos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AgregarProductoActivity extends AppCompatActivity {

    ImageButton btnImagen;
    EditText etNombre, etCantidad, etPrecio, etDescripcion;
    int idProducto = -1; // Por defecto, -1 = nuevo producto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        btnImagen = findViewById(R.id.btnSeleccionarImagen);
        etNombre = findViewById(R.id.etNombreProducto);
        etCantidad = findViewById(R.id.etCantidad);
        etPrecio = findViewById(R.id.etPrecio);
        etDescripcion = findViewById(R.id.etDescripcion);

        btnImagen.setOnClickListener(v ->
                Toast.makeText(this, "Aquí se abriría la galería o cámara", Toast.LENGTH_SHORT).show());

        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());

        Button btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> agregarProducto());

        // Si venimos desde "Editar"
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("producto_id")) {
            idProducto = intent.getIntExtra("producto_id", -1);
            etNombre.setText(intent.getStringExtra("nombre"));
            etCantidad.setText(String.valueOf(intent.getIntExtra("cantidad", 0)));
            etPrecio.setText(String.valueOf(intent.getDoubleExtra("precio", 0.0)));
            etDescripcion.setText(intent.getStringExtra("descripcion"));
            btnAgregar.setText("Guardar cambios");
        }
    }

    private void agregarProducto() {
        String nombre = etNombre.getText().toString().trim();
        String cantidadStr = etCantidad.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (nombre.isEmpty() || cantidadStr.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad;
        double precio;

        try {
            cantidad = Integer.parseInt(cantidadStr);
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad o precio inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        EZPOSSQLiteHelper dbHelper = new EZPOSSQLiteHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("cantidad", cantidad);
        values.put("precio", precio);
        values.put("descripcion", descripcion);

        if (idProducto == -1) {
            // Nuevo producto
            long resultado = db.insert("productos", null, values);
            db.close();

            if (resultado != -1) {
                Toast.makeText(this, "Producto añadido correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Actualización
            int filas = db.update("productos", values, "id = ?", new String[]{String.valueOf(idProducto)});
            db.close();

            if (filas > 0) {
                Toast.makeText(this, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
