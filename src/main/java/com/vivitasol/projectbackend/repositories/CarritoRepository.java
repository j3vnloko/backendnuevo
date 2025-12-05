package com.vivitasol.projectbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vivitasol.projectbackend.entities.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Carrito findByUsuarioId(Long usuarioId);
}
