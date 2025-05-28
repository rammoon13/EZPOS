package com.example.ezpos.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.ezpos.model.Producto;
import com.example.ezpos.repository.ProductoRepository;

import java.util.List;

public class ProductoViewModel extends AndroidViewModel {

    private final ProductoRepository productoRepository;
    private final LiveData<List<Producto>> todosLosProductos;

    public ProductoViewModel(@NonNull Application application) {
        super(application);
        productoRepository = new ProductoRepository(application);
        todosLosProductos = productoRepository.getTodosLosProductos();
    }

    public LiveData<List<Producto>> getTodosLosProductos() {
        return todosLosProductos;
    }

    public void insertarProducto(Producto producto) {
        productoRepository.insertar(producto);
    }

    public void actualizarProducto(Producto producto) {
        productoRepository.actualizar(producto);
    }

    public void eliminarProducto(Producto producto) {
        productoRepository.eliminar(producto);
    }
}
