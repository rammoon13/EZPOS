package com.example.ezpos;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezpos.adapter.PedidoAdapter;
import com.example.ezpos.model.Pedido;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etBuscarCliente;
    private RecyclerView rvPedidos;
    private PedidoAdapter pedidoAdapter;
    private List<Pedido> listaPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etBuscarCliente = findViewById(R.id.etBuscarCliente);
        rvPedidos = findViewById(R.id.rvPedidos);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Inicializar lista de pedidos (esto se reemplazará con datos reales más adelante)
        listaPedidos = new ArrayList<>();
        // Agregar pedidos de ejemplo
        listaPedidos.add(new Pedido("Cliente 1", 50.0, 60.0, 10.0));
        listaPedidos.add(new Pedido("Cliente 2", 30.0, 30.0, 0.0));

        // Configurar RecyclerView
        pedidoAdapter = new PedidoAdapter(listaPedidos);
        rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        rvPedidos.setAdapter(pedidoAdapter);

        // Manejar navegación inferior
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_pedidos:
                        // Ya estamos en la pantalla de pedidos
                        return true;
                    case R.id.nav_inventario:
                        // Navegar a la pantalla de inventario
                        Toast.makeText(MainActivity.this, "Inventario seleccionado", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_historial:
                        // Navegar a la pantalla de historial
                        Toast.makeText(MainActivity.this, "Historial seleccionado", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
    }
}
