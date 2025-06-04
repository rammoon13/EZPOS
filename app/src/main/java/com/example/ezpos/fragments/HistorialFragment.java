package com.example.ezpos.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
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
import com.example.ezpos.R;
import com.example.ezpos.ResumenPedidoActivity;
import com.example.ezpos.database.DatabaseUtils;
import com.example.ezpos.database.EZPOSSQLiteHelper;
import com.example.ezpos.database.JsonUtils;
import com.example.ezpos.database.Pedido;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HistorialFragment extends Fragment {

    private EZPOSSQLiteHelper dbHelper;
    private LinearLayout listaHistorial;
    private EditText buscarHistorial;
    private EditText fechaDesdeText, fechaHastaText;
    private ImageButton btnLimpiarFechas;
    private List<Pedido> pedidos = new ArrayList<>();
    private Calendar fechaDesde = null;
    private Calendar fechaHasta = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historial, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseUtils.getDatabaseHelper(requireContext());
        listaHistorial = view.findViewById(R.id.listaHistorial);
        buscarHistorial = view.findViewById(R.id.buscarHistorial);
        fechaDesdeText = view.findViewById(R.id.fechaDesdeText);
        fechaHastaText = view.findViewById(R.id.fechaHastaText);
        btnLimpiarFechas = view.findViewById(R.id.btnLimpiarFechas);

        // Evita que se abra el teclado en los campos de fecha
        fechaDesdeText.setShowSoftInputOnFocus(false);
        fechaHastaText.setShowSoftInputOnFocus(false);

        view.findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {
            JsonUtils.cerrarSesion(requireContext());
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        fechaDesdeText.setOnClickListener(v -> mostrarSelectorFecha(true));
        fechaHastaText.setOnClickListener(v -> mostrarSelectorFecha(false));

        btnLimpiarFechas.setOnClickListener(v -> {
            fechaDesde = null;
            fechaHasta = null;
            fechaDesdeText.setText("");
            fechaHastaText.setText("");
            actualizarVisibilidadBotonLimpiarFechas();
            mostrarPedidosFiltrados(buscarHistorial.getText().toString());
        });

        fechaDesdeText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && fechaDesdeText.getText().toString().isEmpty()) {
                fechaDesde = null;
                actualizarVisibilidadBotonLimpiarFechas();
                mostrarPedidosFiltrados(buscarHistorial.getText().toString());
            }
        });

        fechaHastaText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && fechaHastaText.getText().toString().isEmpty()) {
                fechaHasta = null;
                actualizarVisibilidadBotonLimpiarFechas();
                mostrarPedidosFiltrados(buscarHistorial.getText().toString());
            }
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

    private void actualizarVisibilidadBotonLimpiarFechas() {
        if (fechaDesde == null && fechaHasta == null) {
            btnLimpiarFechas.setVisibility(View.GONE);
        } else {
            btnLimpiarFechas.setVisibility(View.VISIBLE);
        }
    }

    private void cargarPedidosDesdeBD() {
        pedidos.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pedidos ORDER BY fecha_hora DESC", null);
        while (cursor.moveToNext()) {
            Pedido pedido = Pedido.fromCursor(cursor);
            pedidos.add(pedido);
        }
        cursor.close();
        db.close();
        mostrarPedidosFiltrados(buscarHistorial.getText().toString());
    }

    private void mostrarPedidosFiltrados(String filtro) {
        listaHistorial.removeAllViews();
        for (Pedido p : pedidos) {
            boolean pasaFiltroTexto = p.getNombreCliente().toLowerCase().contains(filtro.toLowerCase());

            boolean pasaFiltroFecha = true;
            if (fechaDesde != null || fechaHasta != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date fechaPedido = sdf.parse(p.getFechaHora());

                    if (fechaDesde != null && fechaPedido.before(fechaDesde.getTime())) {
                        pasaFiltroFecha = false;
                    }
                    if (fechaHasta != null && fechaPedido.after(fechaHasta.getTime())) {
                        pasaFiltroFecha = false;
                    }
                } catch (ParseException e) {
                    pasaFiltroFecha = false;
                }
            }

            if (pasaFiltroTexto && pasaFiltroFecha) {
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

        try {
            String fechaOriginal = pedido.getFechaHora();
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            Date fecha = formatoEntrada.parse(fechaOriginal);
            tvFecha.setText(formatoSalida.format(fecha));
        } catch (ParseException e) {
            tvFecha.setText(pedido.getFechaHora());
        }

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

    private void mostrarSelectorFecha(boolean esDesde) {
        Calendar calendario = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar seleccionada = Calendar.getInstance();
                    seleccionada.set(year, month, dayOfMonth, 0, 0, 0);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                    if (esDesde) {
                        fechaDesde = seleccionada;
                        fechaDesdeText.setText(sdf.format(seleccionada.getTime()));
                    } else {
                        fechaHasta = seleccionada;
                        seleccionada.set(Calendar.HOUR_OF_DAY, 23);
                        seleccionada.set(Calendar.MINUTE, 59);
                        seleccionada.set(Calendar.SECOND, 59);
                        fechaHastaText.setText(sdf.format(seleccionada.getTime()));
                    }

                    actualizarVisibilidadBotonLimpiarFechas();
                    mostrarPedidosFiltrados(buscarHistorial.getText().toString());
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }
}
