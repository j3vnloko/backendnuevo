package com.vivitasol.projectbackend.services;

import com.vivitasol.projectbackend.entities.Carrito;

public interface CarritoService {

    Carrito obtenerCarrito(Long usuarioId);

    Carrito agregarProducto(Long usuarioId, Long productoId, Integer cantidad);

    Carrito actualizarCantidad(Long usuarioId, Long itemId, Integer cantidad);

    Carrito eliminarItem(Long usuarioId, Long itemId);

    void vaciarCarrito(Long usuarioId);
}