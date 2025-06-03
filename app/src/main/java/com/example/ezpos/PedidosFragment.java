package com.example.ezpos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PedidosFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);

        // Referencia al botón
        Button btnIniciar = view.findViewById(R.id.btnIniciarPedido);

        // Evento para abrir NuevoPedidoActivity
        btnIniciar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NuevoPedidoActivity.class);
            startActivity(intent);
        });

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> {
            JsonUtils.cerrarSesion(requireContext());
            Intent intent = new Intent(requireContext(), MainActivity.class); // Login
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        cargarPedidosDesdeBD(); // método que implementaremos ahora
    }
    private void cargarPedidosDesdeBD() {
        LinearLayout listaPedidos = getView().findViewById(R.id.listaPedidos);
        listaPedidos.removeAllViews();

        SQLiteDatabase db = new EZPOSSQLiteHelper(requireContext()).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pedidos ORDER BY id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente"));
            String fechaHora = cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora"));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            double pagado = cursor.getDouble(cursor.getColumnIndexOrThrow("pagado"));
            double devolver = cursor.getDouble(cursor.getColumnIndexOrThrow("devolver"));

            View cardView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_pedido, listaPedidos, false);

            TextView tvNombreCliente = cardView.findViewById(R.id.tvNombreCliente);
            TextView tvTotal = cardView.findViewById(R.id.tvTotalPedido);
            TextView tvPagado = cardView.findViewById(R.id.tvPagadoPedido);
            TextView tvDevolver = cardView.findViewById(R.id.tvADevolverPedido);

            tvNombreCliente.setText(nombreCliente + " — " + fechaHora);
            tvTotal.setText("Total: " + String.format("%.2f", total) + " €");
            tvPagado.setText("Pagado: " + String.format("%.2f", pagado) + " €");
            tvDevolver.setText("A Devolver: " + String.format("%.2f", devolver) + " €");

            listaPedidos.addView(cardView);
        }

        cursor.close();
        db.close();
    }

}
