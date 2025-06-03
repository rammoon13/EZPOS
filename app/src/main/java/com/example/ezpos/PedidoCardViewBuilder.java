package com.example.ezpos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class PedidoCardViewBuilder {

    public static View crear(Context context, Pedido pedido) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_pedido, null);

        TextView nombreCliente = view.findViewById(R.id.tvNombreCliente);
        TextView total = view.findViewById(R.id.tvTotalPedido);
        TextView pagado = view.findViewById(R.id.tvPagadoPedido);
        TextView devolver = view.findViewById(R.id.tvADevolverPedido);

        nombreCliente.setText(pedido.getNombreCliente());
        total.setText("Total: " + pedido.getTotal() + "€");
        pagado.setText("Pagado: " + pedido.getPagado() + "€");
        devolver.setText("A Devolver: " + pedido.getADevolver() + "€");

        return view;
    }
}
