package com.example.ezpos;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import java.util.*;

public class HistorialFragment extends Fragment {

    private EZPOSSQLiteHelper dbHelper;
    private LinearLayout listaHistorial;
    private EditText buscarHistorial;
    private List<Pedido> pedidos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historial, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new EZPOSSQLiteHelper(requireContext());
        listaHistorial = view.findViewById(R.id.listaHistorial);
        buscarHistorial = view.findViewById(R.id.buscarHistorial);

        view.findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {
            JsonUtils.cerrarSesion(requireContext());
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        buscarHistorial.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                mostrarPedidosFiltrados(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        cargarPedidosDesdeBD();
    }

    private void cargarPedidosDesdeBD() {
        pedidos.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pedidos", null);
        while (cursor.moveToNext()) {
            Pedido pedido = Pedido.fromCursor(cursor); // debes tener este método
            pedidos.add(pedido);
        }
        cursor.close();
        db.close();
        mostrarPedidosFiltrados(buscarHistorial.getText().toString());
    }

    private void mostrarPedidosFiltrados(String filtro) {
        listaHistorial.removeAllViews();
        for (Pedido p : pedidos) {
            if (p.getNombreCliente().toLowerCase().contains(filtro.toLowerCase())) {
                listaHistorial.addView(crearCardPedido(p));
            }
        }
    }

    private View crearCardPedido(Pedido pedido) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View card = inflater.inflate(R.layout.card_pedido, null);

        TextView tvFecha = card.findViewById(R.id.tvFechaPedido);
        TextView tvNombre = card.findViewById(R.id.tvNombreCliente);
        TextView tvTotal = card.findViewById(R.id.tvTotalPedido);
        TextView tvPagado = card.findViewById(R.id.tvPagadoPedido);
        TextView tvDevolver = card.findViewById(R.id.tvADevolverPedido);

        tvFecha.setText(pedido.getFechaHora().split(" ")[0]);
        tvNombre.setText(pedido.getNombreCliente());
        tvTotal.setText(String.format("Total: %.2f€", pedido.getTotal()));
        tvPagado.setText(String.format("Pagado: %.2f€", pedido.getPagado()));
        tvDevolver.setText(String.format("A Devolver: %.2f€", pedido.getADevolver()));

        if (pedido.isCambioDevuelto() || pedido.getADevolver() <= 0.01) {
            tvDevolver.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvDevolver.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        card.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ResumenPedidoActivity.class);
            intent.putExtra("id_pedido", pedido.getId());
            startActivity(intent);
        });

        card.setOnLongClickListener(v -> {
            int rojo = getResources().getColor(android.R.color.holo_red_dark);
            int verde = getResources().getColor(android.R.color.holo_green_dark);
            int colorActual = tvDevolver.getCurrentTextColor();

            PopupMenu popup = new PopupMenu(requireContext(), card);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (pedido.isEntregado()) {
                popup.getMenu().add("Deshacer entrega").setOnMenuItemClickListener(item -> {
                    db.execSQL("UPDATE pedidos SET entregado = 0 WHERE id = ?", new Object[]{pedido.getId()});
                    Toast.makeText(requireContext(), "Entrega deshecha", Toast.LENGTH_SHORT).show();
                    db.close();
                    cargarPedidosDesdeBD();
                    return true;
                });
            }

            popup.getMenu().add("Borrar pedido").setOnMenuItemClickListener(item -> {
                db.execSQL("DELETE FROM pedidos WHERE id = ?", new Object[]{pedido.getId()});
                Toast.makeText(requireContext(), "Pedido borrado", Toast.LENGTH_SHORT).show();
                db.close();
                cargarPedidosDesdeBD();
                return true;
            });

            popup.show();
            return true;
        });

        return card;
    }

}
