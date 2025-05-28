package com.example.ezpos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezpos.R;
import com.example.ezpos.model.Pedido;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private List<Pedido> listaPedidos;

    public PedidoAdapter(List<Pedido> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);
        holder.tvCliente.setText(pedido.getCliente());
        holder.tvTotal.setText("Total: $" + pedido.getTotal());
        holder.tvPagado.setText("Pagado: $" + pedido.getPagado());
        holder.tvCambio.setText("Cambio: $" + pedido.getCambio());
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvCliente, tvTotal, tvPagado, tvCambio;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvPagado = itemView.findViewById(R.id.tvPagado);
            tvCambio = itemView.findViewById(R.id.tvCambio);
        }
    }
}
