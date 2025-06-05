package com.example.ezpos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.ezpos.database.DatabaseUtils;
import com.example.ezpos.database.EZPOSSQLiteHelper;
import com.example.ezpos.database.Producto;
import com.example.ezpos.IntroHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Actividad para seleccionar productos y componer un nuevo pedido.
 * Maneja tanto la interfaz de búsqueda como el control de stock temporal.
 */

public class NuevoPedidoActivity extends AppCompatActivity {

    private HashMap<Integer, Integer> productosSeleccionados = new HashMap<>();
    private HashMap<Integer, Producto> productosDisponibles = new HashMap<>();
    private List<Producto> productosFiltrados = new ArrayList<>();
    private EZPOSSQLiteHelper dbHelper;
    private LinearLayout listaProductos, listaSeleccionados;
    private EditText buscador, etNombreCliente;
    private double totalPedido = 0.0;
    private String nombreCliente = "Cliente Genérico";

    @Override
    /**
     * Inicializa los listados y carga los productos desde la base de datos.
     * Al cerrar sin confirmar se revierte el stock descontado temporalmente.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        dbHelper = DatabaseUtils.getDatabaseHelper(this);
        listaProductos = findViewById(R.id.listaProductosDisponibles);
        listaSeleccionados = findViewById(R.id.listaProductosSeleccionados);
        buscador = findViewById(R.id.etBuscarProducto);
        etNombreCliente = findViewById(R.id.etNombreCliente);

        IntroHelper.showIntro(this, "nuevo_pedido", getString(R.string.intro_nuevo_pedido));

        Button btnCancelar = findViewById(R.id.btnCancelarPedido);
        btnCancelar.setOnClickListener(v -> {
            revertirStock();
            finish();
        });

        Button btnContinuar = findViewById(R.id.btnContinuarPedido);
        btnContinuar.setOnClickListener(v -> {
            if (!etNombreCliente.getText().toString().isEmpty()) {
                nombreCliente = etNombreCliente.getText().toString();
            }

            Intent intent = new Intent(this, ResumenPedidoActivity.class);
            intent.putExtra("cliente", nombreCliente);
            intent.putExtra("fecha", obtenerFechaHoraActual());

            JSONArray productosJSON = new JSONArray();
            for (int id : productosSeleccionados.keySet()) {
                Producto p = productosDisponibles.get(id);
                int cantidad = productosSeleccionados.get(id);
                for (int i = 0; i < cantidad; i++) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("id", p.getId());
                        obj.put("nombre", p.getNombre());
                        obj.put("precio", p.getPrecio());
                        productosJSON.put(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            intent.putExtra("productos_seleccionados", productosJSON.toString());
            startActivity(intent);
        });

        buscador.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarProductos(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        cargarProductosDesdeBD();
    }

    private void cargarProductosDesdeBD() {
        // Lee todos los productos de la base de datos para mostrarlos al usuario
        productosDisponibles.clear();
        productosFiltrados.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM productos", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));
            double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
            String imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"));
            Producto producto = new Producto(id, nombre, cantidad, precio, descripcion, imagen);

            productosDisponibles.put(id, producto);
            productosFiltrados.add(producto);
        }

        cursor.close();
        db.close();

        filtrarProductos(buscador.getText().toString());
    }

    private void filtrarProductos(String texto) {
        // Filtra la lista al escribir en el buscador
        listaProductos.removeAllViews();
        String filtro = texto.toLowerCase();

        for (Producto p : productosFiltrados) {
            if (p.getNombre().toLowerCase().contains(filtro)) {
                listaProductos.addView(crearTarjetaProducto(p));
            }
        }
    }

    private View crearTarjetaProducto(Producto producto) {
        // Genera dinámicamente la vista de cada producto disponible
        View cardView = getLayoutInflater().inflate(R.layout.card_producto_disponible, null);
        TextView txtNombre = cardView.findViewById(R.id.txtNombreProducto);
        TextView txtCantidad = cardView.findViewById(R.id.txtCantidadDisponible);
        TextView txtPrecio = cardView.findViewById(R.id.txtPrecio);
        ImageView img = cardView.findViewById(R.id.imgProducto);

        txtNombre.setText(producto.getNombre());
        txtCantidad.setText("Quedan: " + producto.getCantidad() + " uds");
        txtPrecio.setText(String.format(Locale.getDefault(), "%.2f€", producto.getPrecio()));

        if (producto.getImagen() != null && !producto.getImagen().isEmpty()) {
            File imgFile = new File(producto.getImagen());
            if (imgFile.exists()) {
                img.setImageURI(Uri.fromFile(imgFile));
            }
        }

        if (producto.getCantidad() <= 0) {
            cardView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            cardView.setEnabled(false);
        } else {
            cardView.setOnClickListener(v -> {
                reducirStock(producto.getId());
                productosSeleccionados.put(producto.getId(), productosSeleccionados.getOrDefault(producto.getId(), 0) + 1);
                totalPedido += producto.getPrecio();
                actualizarListaSeleccionados();
                cargarProductosDesdeBD(); // Refrescar cantidades
            });
        }

        return cardView;
    }

    private void actualizarListaSeleccionados() {
        // Muestra y permite deshacer productos añadidos al pedido
        listaSeleccionados.removeAllViews();
        for (int id : productosSeleccionados.keySet()) {
            int cantidad = productosSeleccionados.get(id);
            Producto producto = productosDisponibles.get(id);
            if (producto != null && cantidad > 0) {
                CardView tarjeta = new CardView(this);
                tarjeta.setCardElevation(3);
                tarjeta.setRadius(12);
                tarjeta.setUseCompatPadding(true);
                tarjeta.setCardBackgroundColor(getResources().getColor(android.R.color.holo_green_light));

                TextView texto = new TextView(this);
                texto.setPadding(20, 20, 20, 20);
                texto.setTextColor(getResources().getColor(android.R.color.black));
                texto.setText(producto.getNombre() + " x" + cantidad + "    " + String.format(Locale.getDefault(), "%.2f€", producto.getPrecio() * cantidad));

                tarjeta.setOnClickListener(v -> {
                    productosSeleccionados.put(id, cantidad - 1);
                    totalPedido -= producto.getPrecio();
                    sumarStock(id);
                    if (productosSeleccionados.get(id) == 0) productosSeleccionados.remove(id);
                    actualizarListaSeleccionados();
                    cargarProductosDesdeBD();
                });

                tarjeta.addView(texto);
                listaSeleccionados.addView(tarjeta);
            }
        }
    }

    private void reducirStock(int idProducto) {
        // Descuenta una unidad en la base de datos al seleccionar un producto
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE productos SET cantidad = cantidad - 1 WHERE id = ?", new Object[]{idProducto});
        db.close();
    }

    private void sumarStock(int idProducto) {
        // Restaura el stock cuando se elimina un producto del pedido
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE productos SET cantidad = cantidad + 1 WHERE id = ?", new Object[]{idProducto});
        db.close();
    }

    private void revertirStock() {
        // Devuelve el stock a su valor original si se cancela el pedido
        for (int id : productosSeleccionados.keySet()) {
            int cantidad = productosSeleccionados.get(id);
            for (int i = 0; i < cantidad; i++) {
                sumarStock(id);
            }
        }
        productosSeleccionados.clear();
        totalPedido = 0.0;
    }

    private String obtenerFechaHoraActual() {
        // Utilidad para almacenar la hora de creación del pedido
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
