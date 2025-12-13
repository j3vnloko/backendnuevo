package com.vivitasol.projectbackend.dto;

public class CantidadRequest {
    private Integer cantidad;

    // Constructor vacío
    public CantidadRequest() {}

    // Constructor con parámetros
    public CantidadRequest(Integer cantidad) {
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}