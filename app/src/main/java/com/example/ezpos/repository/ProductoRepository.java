package com.example.ezpos.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.ezpos.database.EZPOSDatabase;
import com.example.ezpos.database.ProductoDao;
import com.example.ezpos.model.Producto;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductoRepository {

    private final ProductoDao productoDao;
    private final LiveData<List<Producto>> todosLosProductos;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ProductoRepository(Application application) {
        EZPOSDatabase db = EZPOSDatabase.obtenerInstancia(application);
        productoDao = db.productoDao();
        todosLosProductos = productoDao.obtenerTodos();
    }

    public LiveData<List<Producto>> getTodosLosProductos() {
        return todosLosProductos;
    }

    public void insertar(Producto producto) {
        executor.execute(() -> productoDao.insertarProducto(producto));
    }

    public void actualizar(Producto producto) {
        executor.execute(() -> productoDao.actualizarProducto(producto));
    }

    public void eliminar(Producto producto) {
        executor.execute(() -> productoDao.eliminarProducto(producto));
    }
}
