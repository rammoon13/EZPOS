package com.example.ezpos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ResumenPedidoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_pedido);

        // Botón "Atrás"
        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());

        // Botón "Confirmar pedido"
        Button btnConfirmar = findViewById(R.id.btnConfirmar);
        btnConfirmar.setOnClickListener(v -> {
            Toast.makeText(ResumenPedidoActivity.this, "Pedido confirmado con éxito", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ResumenPedidoActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}