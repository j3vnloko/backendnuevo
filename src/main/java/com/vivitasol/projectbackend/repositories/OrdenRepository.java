package com.vivitasol.projectbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vivitasol.projectbackend.entities.Orden;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
}
