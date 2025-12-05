package com.vivitasol.projectbackend.services;

import java.util.List;

import com.vivitasol.projectbackend.entities.Usuario;

public interface UsuarioServices {
    Usuario registrar(Usuario usuario);
    Usuario obtenerPorEmail(String email);
    List<Usuario> listar();
    Usuario actualizar(Long id, Usuario usuario);
    void eliminar(Long id);
    
    Usuario login(String email, String password);
}
