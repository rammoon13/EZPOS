package com.example.ezpos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class InventarioFragment extends Fragment {

    private LinearLayout contenedor;
    private EditText buscador;
    private List<Producto> listaProductos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);

        Button btnAgregarProducto = view.findViewById(R.id.btnAgregarProducto);
        btnAgregarProducto.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AgregarProductoActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contenedor = view.findViewById(R.id.listaInventario);
        buscador = view.findViewById(R.id.buscarHistorial);

        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarProductos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ImageButton btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> {
            JsonUtils.cerrarSesion(requireContext());
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mostrarProductos();
    }

    private void mostrarProductos() {
        listaProductos.clear();

        EZPOSSQLiteHelper dbHelper = DatabaseUtils.getDatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM productos", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));
            double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
            String imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"));

            listaProductos.add(new Producto(id, nombre, cantidad, precio, descripcion, imagen));
        }

        cursor.close();
        db.close();

        filtrarProductos(buscador != null ? buscador.getText().toString() : "");
    }

    private void filtrarProductos(String texto) {
        contenedor.removeAllViews();
        String filtro = texto.toLowerCase();

        for (Producto producto : listaProductos) {
            View card = ProductoCardViewBuilder.crear(requireContext(), producto);

            // Al hacer clic en la tarjeta, abrimos la vista solo lectura
            card.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), AgregarProductoActivity.class);
                intent.putExtra("producto_id", producto.getId());
                intent.putExtra("nombre", producto.getNombre());
                intent.putExtra("cantidad", producto.getCantidad());
                intent.putExtra("precio", producto.getPrecio());
                intent.putExtra("descripcion", producto.getDescripcion());
                intent.putExtra("imagen", producto.getImagen());
                intent.putExtra("soloLectura", true);  // <<--- NUEVO
                startActivity(intent);
            });

            contenedor.addView(card);
        }
    }

}
