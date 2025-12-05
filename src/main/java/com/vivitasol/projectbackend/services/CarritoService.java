package com.vivitasol.projectbackend.services;

import com.vivitasol.projectbackend.entities.Carrito;

public interface CarritoService {

    Carrito obtenerCarrito(Long usuarioId);

    Carrito agregarProducto(Long usuarioId, Long productoId, Integer cantidad);

    void vaciarCarrito(Long usuarioId);
}
