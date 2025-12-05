package com.vivitasol.projectbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vivitasol.projectbackend.entities.CarritoItem;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
}
