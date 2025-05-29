package com.example.ezpos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
}
