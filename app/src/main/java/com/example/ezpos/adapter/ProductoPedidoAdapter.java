package com.example.ezpos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezpos.R;
import com.example.ezpos.model.Producto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductoPedidoAdapter extends RecyclerView.Adapter<ProductoPedidoAdapter.ProductoViewHolder> {

    private List<Producto> listaProductos = new ArrayList<>();
    private final HashMap<Integer, Integer> cantidades = new HashMap<>();

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_pedido, parent, false);
        return new ProductoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);
        int cantidad = cantidades.getOrDefault(producto.id, 0);

        holder.tvNombre.setText(producto.nombre);
        holder.tvPrecio.setText(String.format("%.2f €", producto.precio));
        holder.tvCantidad.setText(String.valueOf(cantidad));

        holder.btnSumar.setOnClickListener(v -> {
            int nuevaCantidad = cantidad + 1;
            cantidades.put(producto.id, nuevaCantidad);
            notifyItemChanged(position);
        });

        holder.btnRestar.setOnClickListener(v -> {
            int nuevaCantidad = Math.max(0, cantidad - 1);
            cantidades.put(producto.id, nuevaCantidad);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public void setListaProductos(List<Producto> productos) {
        this.listaProductos = productos;
        notifyDataSetChanged();
    }

    public List<Producto> getProductosConCantidad() {
        List<Producto> seleccionados = new ArrayList<>();
        for (Producto p : listaProductos) {
            int cantidad = cantidades.getOrDefault(p.id, 0);
            if (cantidad > 0) {
                Producto copia = new Producto();
                copia.id = p.id;
                copia.nombre = p.nombre;
                copia.precio = p.precio;
                copia.cantidadDisponible = cantidad; // reutilizamos el campo para la cantidad vendida
                seleccionados.add(copia);
            }
        }
        return seleccionados;
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrecio, tvCantidad;
        Button btnSumar, btnRestar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            btnSumar = itemView.findViewById(R.id.btnSumar);
            btnRestar = itemView.findViewById(R.id.btnRestar);
        }
    }
}
