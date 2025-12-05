package com.vivitasol.projectbackend.dto;

public class AuthResponse {

    private final Long id;
    private final String nombre;
    private final String email;
    private final String mensaje;

    public AuthResponse(Long id, String nombre, String email, String mensaje) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.mensaje = mensaje;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getMensaje() {
        return mensaje;
    }
}
