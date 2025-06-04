package com.example.ezpos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ezpos.database.DatabaseUtils;
import com.example.ezpos.database.EZPOSSQLiteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class ResumenPedidoActivity extends AppCompatActivity {

    TextView tvNombreCliente, tvFecha, tvTotal;
    EditText etPagado, etDevolver;
    LinearLayout listaResumenProductos;
    double totalPedido = 0;
    String nombreCliente;
    String fechaHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_pedido);

        tvNombreCliente = findViewById(R.id.tvNombreCliente);
        tvFecha = findViewById(R.id.tvFecha);
        tvTotal = findViewById(R.id.tvTotal);
        etPagado = findViewById(R.id.etPagado);
        etDevolver = findViewById(R.id.etDevolver);
        listaResumenProductos = findViewById(R.id.listaResumenProductos);

        nombreCliente = getIntent().getStringExtra("cliente");
        fechaHora = getIntent().getStringExtra("fecha");

        tvNombreCliente.setText(nombreCliente);
        tvFecha.setText(fechaHora);

        int idPedido = getIntent().getIntExtra("id_pedido", -1);
        if (idPedido != -1) {
            cargarDatosPedidoExistente(idPedido);
        } else {
            mostrarProductosSeleccionados();
        }

        // Actualiza el cambio automáticamente al escribir
        etPagado.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                calcularCambio();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());

        Button btnConfirmar = findViewById(R.id.btnConfirmar);
        btnConfirmar.setOnClickListener(v -> confirmarPedido());
    }

    private void mostrarProductosSeleccionados() {
        listaResumenProductos.removeAllViews();
        totalPedido = 0;

        try {
            JSONArray productos = new JSONArray(getIntent().getStringExtra("productos_seleccionados"));
            for (int i = 0; i < productos.length(); i++) {
                JSONObject producto = productos.getJSONObject(i);
                String nombre = producto.getString("nombre");
                double precio = producto.getDouble("precio");

                LinearLayout fila = new LinearLayout(this);
                fila.setOrientation(LinearLayout.HORIZONTAL);
                fila.setPadding(4, 4, 4, 4);

                TextView nombreTxt = new TextView(this);
                nombreTxt.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                nombreTxt.setText(nombre);

                TextView precioTxt = new TextView(this);
                precioTxt.setText(String.format(Locale.getDefault(), "%.2f€", precio));

                fila.addView(nombreTxt);
                fila.addView(precioTxt);

                listaResumenProductos.addView(fila);
                totalPedido += precio;
            }

            tvTotal.setText(String.format(Locale.getDefault(), "%.2f€", totalPedido));
        } catch (JSONException e) {
            Toast.makeText(this, "Error al leer productos", Toast.LENGTH_SHORT).show();
        }
    }

    private void calcularCambio() {
        String pagadoStr = etPagado.getText().toString().trim();
        if (!pagadoStr.isEmpty()) {
            try {
                double pagado = Double.parseDouble(pagadoStr);
                double devolver = pagado - totalPedido;
                etDevolver.setText(String.format(Locale.getDefault(), "%.2f", devolver));
            } catch (NumberFormatException e) {
                etDevolver.setText("0.00");
            }
        } else {
            etDevolver.setText("0.00");
        }
    }

    private void confirmarPedido() {
        try {
            JSONArray productos = new JSONArray(getIntent().getStringExtra("productos_seleccionados"));

            String pagadoStr = etPagado.getText().toString().trim();
            if (pagadoStr.isEmpty()) {
                Toast.makeText(this, "Introduce el importe pagado", Toast.LENGTH_SHORT).show();
                return;
            }

            double pagado;
            try {
                pagado = Double.parseDouble(pagadoStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "El importe pagado no es válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pagado < totalPedido) {
                Toast.makeText(this, "El importe es insuficiente", Toast.LENGTH_SHORT).show();
                return;
            }

            double devolver = pagado - totalPedido;

            EZPOSSQLiteHelper dbHelper = DatabaseUtils.getDatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues pedidoValues = new ContentValues();
            pedidoValues.put("nombre_cliente", nombreCliente);
            pedidoValues.put("fecha_hora", fechaHora);
            pedidoValues.put("total", totalPedido);
            pedidoValues.put("pagado", pagado);
            pedidoValues.put("devolver", devolver);

            long idPedido = db.insert("pedidos", null, pedidoValues);

            for (int i = 0; i < productos.length(); i++) {
                JSONObject producto = productos.getJSONObject(i);
                int idProducto = producto.getInt("id");
                double precio = producto.getDouble("precio");

                ContentValues detalle = new ContentValues();
                detalle.put("id_pedido", idPedido);
                detalle.put("id_producto", idProducto);
                detalle.put("cantidad", 1);
                detalle.put("precio_unitario", precio);

                db.insert("detalle_pedido", null, detalle);
            }

            db.close();

            Toast.makeText(this, "Pedido confirmado con éxito", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();

        } catch (JSONException e) {
            Toast.makeText(this, "Error al procesar el pedido", Toast.LENGTH_SHORT).show();
        }
    }
    private void cargarDatosPedidoExistente(int idPedido) {
        EZPOSSQLiteHelper dbHelper = DatabaseUtils.getDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursorPedido = db.rawQuery("SELECT * FROM pedidos WHERE id = ?", new String[]{String.valueOf(idPedido)});
        if (cursorPedido.moveToFirst()) {
            nombreCliente = cursorPedido.getString(cursorPedido.getColumnIndexOrThrow("nombre_cliente"));
            fechaHora = cursorPedido.getString(cursorPedido.getColumnIndexOrThrow("fecha_hora"));
            totalPedido = cursorPedido.getDouble(cursorPedido.getColumnIndexOrThrow("total"));
            double pagado = cursorPedido.getDouble(cursorPedido.getColumnIndexOrThrow("pagado"));
            double devolver = cursorPedido.getDouble(cursorPedido.getColumnIndexOrThrow("devolver"));

            tvNombreCliente.setText(nombreCliente);
            tvFecha.setText(fechaHora);
            tvTotal.setText(String.format("%.2f€", totalPedido));
            etPagado.setText(String.format("%.2f", pagado));
            etDevolver.setText(String.format("%.2f", devolver));
        }
        cursorPedido.close();

        // Cargar productos
        JSONArray productos = new JSONArray();
        Cursor cursorProductos = db.rawQuery(
                "SELECT p.id, p.nombre, dp.precio_unitario FROM detalle_pedido dp INNER JOIN productos p ON p.id = dp.id_producto WHERE dp.id_pedido = ?",
                new String[]{String.valueOf(idPedido)}
        );
        while (cursorProductos.moveToNext()) {
            JSONObject prod = new JSONObject();
            try {
                prod.put("id", cursorProductos.getInt(cursorProductos.getColumnIndexOrThrow("id")));
                prod.put("nombre", cursorProductos.getString(cursorProductos.getColumnIndexOrThrow("nombre")));
                prod.put("precio", cursorProductos.getDouble(cursorProductos.getColumnIndexOrThrow("precio_unitario")));
                productos.put(prod);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursorProductos.close();
        db.close();

        try {
            mostrarProductosResumen(productos);
        } catch (Exception e) {
            Toast.makeText(this, "Error al mostrar productos", Toast.LENGTH_SHORT).show();
        }

        // Ocultar botón confirmar para pedidos antiguos
        Button btnConfirmar = findViewById(R.id.btnConfirmar);
        btnConfirmar.setVisibility(View.GONE);
        etPagado.setEnabled(false);
        etDevolver.setEnabled(false);

    }
    private void mostrarProductosResumen(JSONArray productos) {
        listaResumenProductos.removeAllViews();
        totalPedido = 0;

        for (int i = 0; i < productos.length(); i++) {
            try {
                JSONObject producto = productos.getJSONObject(i);
                String nombre = producto.getString("nombre");
                double precio = producto.getDouble("precio");

                LinearLayout fila = new LinearLayout(this);
                fila.setOrientation(LinearLayout.HORIZONTAL);
                fila.setPadding(4, 4, 4, 4);

                TextView nombreTxt = new TextView(this);
                nombreTxt.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                nombreTxt.setText(nombre);

                TextView precioTxt = new TextView(this);
                precioTxt.setText(String.format(Locale.getDefault(), "%.2f€", precio));

                fila.addView(nombreTxt);
                fila.addView(precioTxt);

                listaResumenProductos.addView(fila);
                totalPedido += precio;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        tvTotal.setText(String.format(Locale.getDefault(), "%.2f€", totalPedido));
    }

}
