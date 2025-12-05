package com.vivitasol.projectbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vivitasol.projectbackend.entities.OrdenItem;

public interface OrdenItemRepository extends JpaRepository<OrdenItem, Long> {
}
