package com.vivitasol.projectbackend.dto;

public class CarritoItemRequest {
    private Long productoId;
    private Integer cantidad;

    // Constructor vacío
    public CarritoItemRequest() {}

    // Constructor con parámetros
    public CarritoItemRequest(Long productoId, Integer cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}