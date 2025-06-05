/**
 * Fragmento para consultar el historial completo de pedidos con filtros por fecha.
 */
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
import com.example.ezpos.IntroHelper;

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
    private Map<String, Double> gananciasPorDia = new HashMap<>();
    private Calendar fechaDesde = null;
    private Calendar fechaHasta = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Layout con filtros y lista de ventas antiguas
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

        IntroHelper.showIntro(requireContext(), "historial", getString(R.string.intro_historial));

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

        // Filtro en tiempo real por nombre de cliente
        buscarHistorial.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                mostrarPedidosFiltrados(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        cargarPedidosDesdeBD();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarPedidosDesdeBD();
    }

    private void actualizarVisibilidadBotonLimpiarFechas() {
        // Muestra u oculta el icono para borrar filtros de fecha
        if (fechaDesde == null && fechaHasta == null) {
            btnLimpiarFechas.setVisibility(View.GONE);
        } else {
            btnLimpiarFechas.setVisibility(View.VISIBLE);
        }
    }

    private void cargarPedidosDesdeBD() {
        // Extrae todos los pedidos de la base de datos ordenados por fecha
        pedidos.clear();
        gananciasPorDia.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pedidos ORDER BY fecha_hora DESC", null);
        while (cursor.moveToNext()) {
            Pedido pedido = Pedido.fromCursor(cursor);
            pedidos.add(pedido);

            try {
                SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat formatoDia = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                Date fecha = formatoEntrada.parse(pedido.getFechaHora());
                String dia = formatoDia.format(fecha);
                double totalDia = gananciasPorDia.getOrDefault(dia, 0.0);
                gananciasPorDia.put(dia, totalDia + pedido.getTotal());
            } catch (ParseException e) {
                // Ignorar errores de parseo para el calculo de ganancias
            }
        }
        cursor.close();
        db.close();
        mostrarPedidosFiltrados(buscarHistorial.getText().toString());
    }

    private void mostrarPedidosFiltrados(String filtro) {
        // Muestra sólo los pedidos que cumplen el filtro de texto y fecha
        listaHistorial.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat formatoDia = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        SimpleDateFormat formatoCabecera = new SimpleDateFormat("dd 'de' MMMM yyyy", Locale.getDefault());

        String ultimoDia = "";

        for (Pedido p : pedidos) {
            boolean pasaFiltroTexto = p.getNombreCliente().toLowerCase().contains(filtro.toLowerCase());

            boolean pasaFiltroFecha = true;
            if (fechaDesde != null || fechaHasta != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date fechaPedido = sdf.parse(p.getFechaHora());

                    Calendar desde = null;
                    Calendar hasta = null;

                    if (fechaDesde != null) {
                        desde = (Calendar) fechaDesde.clone();
                    }
                    if (fechaHasta != null) {
                        hasta = (Calendar) fechaHasta.clone();
                    }

                    if (fechaDesde != null && fechaHasta == null) {
                        hasta = (Calendar) fechaDesde.clone();
                        hasta.set(Calendar.HOUR_OF_DAY, 23);
                        hasta.set(Calendar.MINUTE, 59);
                        hasta.set(Calendar.SECOND, 59);
                    } else if (fechaHasta != null && fechaDesde == null) {
                        desde = (Calendar) fechaHasta.clone();
                        desde.set(Calendar.HOUR_OF_DAY, 0);
                        desde.set(Calendar.MINUTE, 0);
                        desde.set(Calendar.SECOND, 0);
                    }

                    if (desde != null && fechaPedido.before(desde.getTime())) {
                        pasaFiltroFecha = false;
                    }
                    if (hasta != null && fechaPedido.after(hasta.getTime())) {
                        pasaFiltroFecha = false;
                    }
                } catch (ParseException e) {
                    pasaFiltroFecha = false;
                }
            }

            if (pasaFiltroTexto && pasaFiltroFecha) {
                try {
                    Date fecha = formatoEntrada.parse(p.getFechaHora());
                    String diaActual = formatoDia.format(fecha);

                    if (!diaActual.equals(ultimoDia)) {
                        ultimoDia = diaActual;
                        View header = inflater.inflate(R.layout.item_fecha_header, listaHistorial, false);
                        TextView tvFecha = header.findViewById(R.id.tvFechaHeader);
                        TextView tvGanancias = header.findViewById(R.id.tvGananciasDia);
                        Calendar hoy = Calendar.getInstance();
                        Calendar calPedido = Calendar.getInstance();
                        calPedido.setTime(fecha);
                        if (calPedido.get(Calendar.YEAR) == hoy.get(Calendar.YEAR) &&
                                calPedido.get(Calendar.DAY_OF_YEAR) == hoy.get(Calendar.DAY_OF_YEAR)) {
                            tvFecha.setText(getString(R.string.label_today));
                        } else {
                            tvFecha.setText(formatoCabecera.format(fecha));
                        }
                        double ganancia = gananciasPorDia.getOrDefault(diaActual, 0.0);
                        tvGanancias.setText(String.format(Locale.getDefault(), "%.2f€", ganancia));
                        listaHistorial.addView(header);
                    }
                } catch (ParseException e) {
                    // Ignorar errores de formato y no mostrar cabecera
                }

                listaHistorial.addView(crearCardPedido(p));
            }
        }
    }

    private View crearCardPedido(Pedido pedido) {
        // Genera la tarjeta con las acciones para cada pedido
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
        // Diálogo de calendario para establecer rango de fechas
        Calendar calendario = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar seleccionada = Calendar.getInstance();
                    seleccionada.set(year, month, dayOfMonth, 0, 0, 0);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                    if (esDesde) {
                        if (fechaHasta != null && seleccionada.after(fechaHasta)) {
                            Toast.makeText(requireContext(), R.string.error_fecha_desde_posterior, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fechaDesde = seleccionada;
                        fechaDesdeText.setText(sdf.format(seleccionada.getTime()));
                    } else {
                        if (fechaDesde != null && seleccionada.before(fechaDesde)) {
                            Toast.makeText(requireContext(), R.string.error_fecha_hasta_anterior, Toast.LENGTH_SHORT).show();
                            return;
                        }
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
