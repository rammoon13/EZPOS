package com.example.ezpos;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.ezpos.database.DatabaseUtils;
import com.example.ezpos.database.EZPOSSQLiteHelper;
import com.example.ezpos.IntroHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

// Gestiona la creación y edición de productos. El mismo layout sirve para
// mostrar información o para modificarla según se indique.

public class AgregarProductoActivity extends AppCompatActivity {

    ImageButton btnImagen;
    EditText etNombre, etCantidad, etPrecio, etDescripcion;
    Button btnAgregar;
    TextView tvTitulo;
    int idProducto = -1;
    private static final int REQUEST_GALERIA = 1;
    private static final int REQUEST_CAMARA = 2;
    private String rutaImagenSeleccionada = null;
    private Uri uriFotoCamara = null;
    private boolean soloLectura = false;

    /**
     * Configura la pantalla según si se trata de un producto nuevo o existente.
     * También prepara los botones para seleccionar imagen y guardar los cambios.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        btnImagen = findViewById(R.id.btnSeleccionarImagen);
        etNombre = findViewById(R.id.etNombreProducto);
        etCantidad = findViewById(R.id.etCantidad);
        etPrecio = findViewById(R.id.etPrecio);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnAgregar = findViewById(R.id.btnAgregar);
        tvTitulo = findViewById(R.id.tvTituloProducto); // Título dinámico

        IntroHelper.showIntro(this, "agregar_producto", getString(R.string.intro_agregar_producto));

        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        soloLectura = intent.getBooleanExtra("soloLectura", false);

        // Si venimos a editar o visualizar
        if (intent != null && intent.hasExtra("producto_id")) {
            idProducto = intent.getIntExtra("producto_id", -1);
            etNombre.setText(intent.getStringExtra("nombre"));
            etCantidad.setText(String.valueOf(intent.getIntExtra("cantidad", 0)));
            etPrecio.setText(String.valueOf(intent.getDoubleExtra("precio", 0.0)));
            etDescripcion.setText(intent.getStringExtra("descripcion"));

            rutaImagenSeleccionada = intent.getStringExtra("imagen");
            if (rutaImagenSeleccionada != null) {
                File imgFile = new File(rutaImagenSeleccionada);
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(rutaImagenSeleccionada);
                    btnImagen.setImageBitmap(bitmap);
                }
            }

            if (soloLectura) {
                tvTitulo.setText(intent.getStringExtra("nombre")); // ← Modo vista
                desactivarEdicion();
            } else {
                tvTitulo.setText("Editar Producto"); // ← Modo edición
                btnAgregar.setText("Guardar cambios");
            }
        } else {
            tvTitulo.setText("Agregar Producto"); // ← Modo nuevo
        }

        // Si no es solo lectura, habilitamos botones
        if (!soloLectura) {
            btnAgregar.setOnClickListener(v -> agregarProducto());

            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
            }

            btnImagen.setOnClickListener(v -> mostrarMenuImagen());
        }
    }

    private void desactivarEdicion() {
        // Deshabilita campos para mostrar el producto en modo solo lectura
        etNombre.setEnabled(false);
        etCantidad.setEnabled(false);
        etPrecio.setEnabled(false);
        etDescripcion.setEnabled(false);
        btnImagen.setEnabled(false);
        btnAgregar.setVisibility(View.GONE);
    }

    private void mostrarMenuImagen() {
        // Permite elegir la imagen desde galería o cámara guardando el resultado
        PopupMenu menu = new PopupMenu(this, btnImagen);
        menu.getMenu().add("Elegir de galería").setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_GALERIA);
            return true;
        });
        menu.getMenu().add("Tomar foto").setOnMenuItemClickListener(item -> {
            File imagenArchivo = new File(getFilesDir(), "foto_temp_" + System.currentTimeMillis() + ".jpg");
            try {
                imagenArchivo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo crear el archivo", Toast.LENGTH_SHORT).show();
                return true;
            }

            uriFotoCamara = FileProvider.getUriForFile(this, getPackageName() + ".provider", imagenArchivo);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoCamara);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_CAMARA);
            return true;
        });
        menu.show();
    }

    private void agregarProducto() {
        // Valida los campos y guarda o actualiza el registro en SQLite
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

        EZPOSSQLiteHelper dbHelper = DatabaseUtils.getDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("cantidad", cantidad);
        values.put("precio", precio);
        values.put("descripcion", descripcion);
        values.put("imagen", rutaImagenSeleccionada);

        if (idProducto == -1) {
            long resultado = db.insert("productos", null, values);
            db.close();
            if (resultado != -1) {
                Toast.makeText(this, "Producto añadido correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        } else {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Recibe la foto tomada o seleccionada y la guarda en local
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALERIA && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                        rutaImagenSeleccionada = guardarImagenLocal(bitmap);
                        btnImagen.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == REQUEST_CAMARA && uriFotoCamara != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uriFotoCamara);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    rutaImagenSeleccionada = guardarImagenLocal(bitmap);
                    btnImagen.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String guardarImagenLocal(Bitmap bitmap) {
        // Guarda la imagen tomada en la memoria interna y devuelve su ruta
        try {
            File directorio = new File(getFilesDir(), "imagenes_productos");
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            File archivo = new File(directorio, "producto_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(archivo);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return archivo.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Notifica al usuario si se deniega el permiso de la cámara
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
        }
    }
}
