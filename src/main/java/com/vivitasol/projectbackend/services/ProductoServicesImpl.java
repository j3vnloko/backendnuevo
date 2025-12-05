package com.vivitasol.projectbackend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vivitasol.projectbackend.entities.Producto;
import com.vivitasol.projectbackend.repositories.ProductoRepository;

@Service
public class ProductoServicesImpl implements ProductoServices {

    @Autowired
    private ProductoRepository productoRepositories;

    @Override
    public Producto crear(Producto producto){
        // Por si viene null desde el frontend
        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }
        return productoRepositories.save(producto);
    }

    @Override
    public Producto obtenerId(Long id) {
        return productoRepositories.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }

    @Override
    public List<Producto> listarTodas() {
        return (List<Producto>) productoRepositories.findAll();
    }

    @Override
    public void eliminar(Long id) {
        if (!productoRepositories.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productoRepositories.deleteById(id);
    }

    @Override
    public Producto actualizar(Long id, Producto productoActualizado) {
        Producto existente = obtenerId(id);

        existente.setNombre(productoActualizado.getNombre());
        existente.setDescripcion(productoActualizado.getDescripcion());
        existente.setPrecio(productoActualizado.getPrecio());
        // si en tu entidad tienes stock, agrégalo aquí:
        // existente.setStock(productoActualizado.getStock());
        existente.setActivo(productoActualizado.getActivo());
        existente.setCategoria(productoActualizado.getCategoria());

        return productoRepositories.save(existente);
    }

    @Override
    public Producto desactivar(Long id){
        Producto producto = obtenerId(id);
        producto.setActivo(false);
        return productoRepositories.save(producto);
    }

    @Override
    public Producto activar(Long id){
        Producto producto = obtenerId(id);
        producto.setActivo(true);
        return productoRepositories.save(producto);
    }
}
