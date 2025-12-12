package com.vivitasol.projectbackend.repositories;
import com.vivitasol.projectbackend.entities.Orden;
import com.vivitasol.projectbackend.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuarioOrderByFechaDesc(Usuario usuario);
    List<Orden> findAllByOrderByFechaDesc();
}