package com.example.ezpos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezpos.adapter.ProductoPedidoAdapter;
import com.example.ezpos.model.Producto;
import com.example.ezpos.viewmodel.ProductoViewModel;

import java.util.List;

public class NuevoPedidoActivity extends AppCompatActivity {

    private EditText etNombreCliente;
    private RecyclerView rvProductos;
    private Button btnCancelar, btnContinuar;
    private ProductoPedidoAdapter productoAdapter;
    private ProductoViewModel productoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        etNombreCliente = findViewById(R.id.etNombreCliente);
        rvProductos = findViewById(R.id.rvProductosPedido);
        btnCancelar = findViewById(R.id.btnCancelarPedido);
        btnContinuar = findViewById(R.id.btnContinuarPedido);

        productoAdapter = new ProductoPedidoAdapter();
        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        rvProductos.setAdapter(productoAdapter);

        productoViewModel = new ViewModelProvider(this).get(ProductoViewModel.class);
        productoViewModel.getTodosLosProductos().observe(this, productos -> {
            productoAdapter.setListaProductos(productos);
        });

        btnCancelar.setOnClickListener(v -> finish());

        btnContinuar.setOnClickListener(v -> {
            String nombreCliente = etNombreCliente.getText().toString().trim();
            List<Producto> seleccionados = productoAdapter.getProductosConCantidad();

            if (nombreCliente.isEmpty()) {
                Toast.makeText(this, "Introduce el nombre del cliente", Toast.LENGTH_SHORT).show();
                return;
            }
            if (seleccionados.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos un producto", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: pasar lista a ResumenPedidoActivity (Marco 7)
            Toast.makeText(this, "Pedido preparado. Falta pantalla de resumen.", Toast.LENGTH_SHORT).show();
        });
    }
}
