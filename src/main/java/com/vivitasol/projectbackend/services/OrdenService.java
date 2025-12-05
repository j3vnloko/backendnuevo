package com.vivitasol.projectbackend.services;

import com.vivitasol.projectbackend.entities.Orden;

public interface OrdenService {

    Orden crearOrden(Long usuarioId);

    byte[] generarBoletaPDF(Long ordenId);
}
