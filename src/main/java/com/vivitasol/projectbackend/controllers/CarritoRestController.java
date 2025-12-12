package com.vivitasol.projectbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vivitasol.projectbackend.entities.Carrito;
import com.vivitasol.projectbackend.services.CarritoService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/carrito")
public class CarritoRestController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Carrito> obtener(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(usuarioId));
    }

    @PostMapping("/{usuarioId}/agregar")
    public ResponseEntity<Carrito> agregar(
            @PathVariable Long usuarioId,
            @RequestBody CarritoItemRequest req) {
        return ResponseEntity.ok(
            carritoService.agregarProducto(usuarioId, req.getProductoId(), req.getCantidad())
        );
    }

    @PutMapping("/{usuarioId}/item/{itemId}")
    public ResponseEntity<Carrito> actualizarCantidad(
            @PathVariable Long usuarioId,
            @PathVariable Long itemId,
            @RequestBody CantidadRequest req) {
        return ResponseEntity.ok(
            carritoService.actualizarCantidad(usuarioId, itemId, req.getCantidad())
        );
    }

    @DeleteMapping("/{usuarioId}/item/{itemId}")
    public ResponseEntity<Carrito> eliminarItem(
            @PathVariable Long usuarioId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(
            carritoService.eliminarItem(usuarioId, itemId)
        );
    }

    @DeleteMapping("/{usuarioId}/vaciar")
    public ResponseEntity<Void> vaciar(@PathVariable Long usuarioId) {
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }
}