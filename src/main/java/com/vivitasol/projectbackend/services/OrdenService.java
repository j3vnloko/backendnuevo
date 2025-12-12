package com.vivitasol.projectbackend.services;
import com.vivitasol.projectbackend.entities.Orden;
import java.util.List;

public interface OrdenService {
    Orden crearOrden(Long usuarioId);
    byte[] generarBoletaPDF(Long ordenId);
    List<Orden> obtenerHistorialUsuario(Long usuarioId);
    List<Orden> obtenerTodasLasOrdenes();
}