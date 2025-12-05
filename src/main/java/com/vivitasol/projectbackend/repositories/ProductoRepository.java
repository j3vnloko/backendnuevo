package com.vivitasol.projectbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vivitasol.projectbackend.entities.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
