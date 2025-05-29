package com.example.ezpos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class NuevoPedidoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        Button btnCancelar = findViewById(R.id.btnCancelarPedido);
        btnCancelar.setOnClickListener(v -> {
            finish(); // Vuelve al fragmento anterior
        });
        Button btnContinuar = findViewById(R.id.btnContinuarPedido);
        btnContinuar.setOnClickListener(v -> {
            Intent intent = new Intent(NuevoPedidoActivity.this, ResumenPedidoActivity.class);
            startActivity(intent);
        });
    }
}
