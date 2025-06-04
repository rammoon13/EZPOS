package com.example.ezpos.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ezpos.AgregarProductoActivity;
import com.example.ezpos.MainActivity;
import com.example.ezpos.cardviews.ProductoCardViewBuilder;
import com.example.ezpos.R;
import com.example.ezpos.database.DatabaseUtils;
import com.example.ezpos.database.JsonUtils;
import com.example.ezpos.database.Producto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class InventarioFragment extends Fragment {

    private LinearLayout contenedor;
    private EditText buscador;
    private List<Producto> listaProductos = new ArrayList<>();

    private ActivityResultLauncher<Intent> exportarLauncher;
    private ActivityResultLauncher<Intent> importarLauncher;

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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarProductos(s.toString());
            }
        });

        ImageButton btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> {
            JsonUtils.cerrarSesion(requireContext());
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        ImageButton btnMenuDatos = view.findViewById(R.id.btnMenuDatos);
        btnMenuDatos.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(requireContext(), btnMenuDatos);
            menu.getMenu().add("Exportar datos").setOnMenuItemClickListener(item -> {
                exportarBaseDeDatos();
                return true;
            });
            menu.getMenu().add("Importar datos").setOnMenuItemClickListener(item -> {
                importarBaseDeDatos();
                return true;
            });
            menu.show();
        });

        configurarLaunchers();
    }

    @Override
    public void onResume() {
        super.onResume();
        mostrarProductos();
    }

    private void configurarLaunchers() {
        exportarLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try {
                            String dbName = DatabaseUtils.getNombreBaseDatos(requireContext());
                            File dbFile = requireContext().getDatabasePath(dbName);
                            InputStream in = new FileInputStream(dbFile);
                            OutputStream out = requireContext().getContentResolver().openOutputStream(uri);
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = in.read(buffer)) > 0) {
                                out.write(buffer, 0, length);
                            }
                            in.close();
                            out.close();
                            Toast.makeText(requireContext(), "Base de datos exportada correctamente", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error al exportar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        importarLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try {
                            String dbName = DatabaseUtils.getNombreBaseDatos(requireContext());
                            File dbFile = requireContext().getDatabasePath(dbName);
                            InputStream in = requireContext().getContentResolver().openInputStream(uri);
                            OutputStream out = new FileOutputStream(dbFile);
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = in.read(buffer)) > 0) {
                                out.write(buffer, 0, length);
                            }
                            in.close();
                            out.close();
                            Toast.makeText(requireContext(), "Base de datos importada correctamente", Toast.LENGTH_SHORT).show();
                            mostrarProductos();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error al importar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void exportarBaseDeDatos() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, "ezpos.db");
        exportarLauncher.launch(intent);
    }

    private void importarBaseDeDatos() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        importarLauncher.launch(intent);
    }

    private void mostrarProductos() {
        listaProductos.clear();

        SQLiteDatabase db = DatabaseUtils.getDatabaseHelper(requireContext()).getReadableDatabase();
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

            card.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), AgregarProductoActivity.class);
                intent.putExtra("producto_id", producto.getId());
                intent.putExtra("nombre", producto.getNombre());
                intent.putExtra("cantidad", producto.getCantidad());
                intent.putExtra("precio", producto.getPrecio());
                intent.putExtra("descripcion", producto.getDescripcion());
                intent.putExtra("imagen", producto.getImagen());
                intent.putExtra("soloLectura", true);
                startActivity(intent);
            });

            contenedor.addView(card);
        }
    }
}
