package com.example.ezpos.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ezpos.MainActivity;
import com.example.ezpos.NuevoPedidoActivity;
import com.example.ezpos.R;
import com.example.ezpos.ResumenPedidoActivity;
import com.example.ezpos.database.DatabaseUtils;
import com.example.ezpos.database.JsonUtils;
import com.example.ezpos.database.Pedido;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PedidosFragment extends Fragment {

    private LinearLayout listaPedidos;
    private EditText buscarHistorial;
    private List<Pedido> listaCompletaPedidos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);

        view.findViewById(R.id.btnIniciarPedido).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NuevoPedidoActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listaPedidos = view.findViewById(R.id.listaPedidos);
        buscarHistorial = view.findViewById(R.id.buscarHistorial);

        buscarHistorial.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarPedidos(s.toString());
            }
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
        cargarPedidosDesdeBD();
    }

    private void cargarPedidosDesdeBD() {
        listaCompletaPedidos.clear();

        SQLiteDatabase db = DatabaseUtils.getDatabaseHelper(requireContext()).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pedidos WHERE entregado = 0 ORDER BY id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow("nombre_cliente"));
            String fechaHora = cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora"));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            double pagado = cursor.getDouble(cursor.getColumnIndexOrThrow("pagado"));
            double devolver = cursor.getDouble(cursor.getColumnIndexOrThrow("devolver"));
            boolean cambioDevuelto = cursor.getInt(cursor.getColumnIndexOrThrow("cambio_devuelto")) == 1;
            boolean entregado = false;

            Pedido pedido = new Pedido(id, nombreCliente, fechaHora, null, total, pagado, devolver, cambioDevuelto, entregado);
            listaCompletaPedidos.add(pedido);
        }

        cursor.close();
        db.close();

        filtrarPedidos(buscarHistorial != null ? buscarHistorial.getText().toString() : "");
    }

    private void filtrarPedidos(String texto) {
        listaPedidos.removeAllViews();
        String filtro = texto.toLowerCase();

        for (Pedido pedido : listaCompletaPedidos) {
            if (pedido.getNombreCliente().toLowerCase().contains(filtro)) {
                View cardView = LayoutInflater.from(requireContext()).inflate(R.layout.item_pedido, listaPedidos, false);

                TextView tvNombreCliente = cardView.findViewById(R.id.tvNombreCliente);
                TextView tvTotal = cardView.findViewById(R.id.tvTotalPedido);
                TextView tvPagado = cardView.findViewById(R.id.tvPagadoPedido);
                TextView tvDevolver = cardView.findViewById(R.id.tvADevolverPedido);

                String fechaFormateada;
                try {
                    SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat formatoSalida = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                    Date fecha = formatoEntrada.parse(pedido.getFechaHora());
                    fechaFormateada = formatoSalida.format(fecha);
                } catch (ParseException e) {
                    fechaFormateada = pedido.getFechaHora(); // en caso de error
                }
                tvNombreCliente.setText(pedido.getNombreCliente() + " — " + fechaFormateada);

                tvTotal.setText("Total: " + String.format("%.2f", pedido.getTotal()) + " €");
                tvPagado.setText("Pagado: " + String.format("%.2f", pedido.getPagado()) + " €");
                tvDevolver.setText("A Devolver: " + String.format("%.2f", pedido.getADevolver()) + " €");

                if (pedido.isCambioDevuelto() || pedido.getADevolver() <= 0.01) {
                    tvDevolver.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    tvDevolver.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }

                listaPedidos.addView(cardView);

                cardView.setOnClickListener(v -> {
                    Intent intent = new Intent(requireContext(), ResumenPedidoActivity.class);
                    intent.putExtra("id_pedido", pedido.getId());
                    startActivity(intent);
                });

                cardView.setOnLongClickListener(v -> {
                    int colorActual = tvDevolver.getCurrentTextColor();
                    int rojo = getResources().getColor(android.R.color.holo_red_dark);
                    int verde = getResources().getColor(android.R.color.holo_green_dark);
                    PopupMenu popup = new PopupMenu(requireContext(), cardView);
                    SQLiteDatabase writableDb = DatabaseUtils.getDatabaseHelper(requireContext()).getWritableDatabase();

                    if (colorActual == rojo && pedido.getADevolver() > 0.01 && !pedido.isCambioDevuelto()) {
                        popup.getMenu().add("Entregar cambio").setOnMenuItemClickListener(item -> {
                            writableDb.execSQL("UPDATE pedidos SET cambio_devuelto = 1 WHERE id = ?", new Object[]{pedido.getId()});
                            tvDevolver.setTextColor(verde);
                            Toast.makeText(requireContext(), "Cambio entregado", Toast.LENGTH_SHORT).show();
                            return true;
                        });
                    } else {
                        popup.getMenu().add("Entregar pedido").setOnMenuItemClickListener(item -> {
                            writableDb.execSQL("UPDATE pedidos SET entregado = 1 WHERE id = ?", new Object[]{pedido.getId()});
                            listaPedidos.removeView(cardView);
                            Toast.makeText(requireContext(), "Pedido entregado", Toast.LENGTH_SHORT).show();
                            return true;
                        });
                    }

                    popup.show();
                    return true;
                });
            }
        }
    }

}
