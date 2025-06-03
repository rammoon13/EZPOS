package com.example.ezpos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class ProductoCardViewBuilder {

    public static View crear(Context context, Producto producto) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_producto, null);

        TextView nombre = view.findViewById(R.id.tvNombreProducto);
        TextView cantidad = view.findViewById(R.id.tvCantidadProducto);
        TextView precio = view.findViewById(R.id.tvPrecioProducto);

        nombre.setText(producto.getNombre());
        cantidad.setText("Cantidad: " + producto.getCantidad());
        precio.setText("Precio: " + producto.getPrecio() + "€");

        // Menú contextual al mantener pulsado
        view.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.getMenuInflater().inflate(R.menu.menu_producto, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_editar) {
                    Intent intent = new Intent(context, AgregarProductoActivity.class);
                    intent.putExtra("producto_id", producto.getId());
                    intent.putExtra("nombre", producto.getNombre());
                    intent.putExtra("cantidad", producto.getCantidad());
                    intent.putExtra("precio", producto.getPrecio());
                    intent.putExtra("descripcion", producto.getDescripcion());
                    context.startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.menu_eliminar) {
                    eliminarProducto(context, producto.getId());
                    ((Activity) context).recreate(); // Refrescar vista actual
                    return true;
                }
                return false;
            });

            popup.show();
            return true;
        });

        return view;
    }

    private static void eliminarProducto(Context context, int idProducto) {
        EZPOSSQLiteHelper dbHelper = new EZPOSSQLiteHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filas = db.delete("productos", "id = ?", new String[]{String.valueOf(idProducto)});
        db.close();

        if (filas > 0) {
            Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }
}
