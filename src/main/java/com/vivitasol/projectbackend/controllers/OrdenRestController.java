package com.vivitasol.projectbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vivitasol.projectbackend.entities.Orden;
import com.vivitasol.projectbackend.services.OrdenService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/ordenes")
public class OrdenRestController {

    @Autowired
    private OrdenService ordenService;

    @PostMapping("/{usuarioId}/crear")
    public ResponseEntity<Orden> crearOrden(@PathVariable Long usuarioId) {
        Orden orden = ordenService.crearOrden(usuarioId);
        return ResponseEntity.ok(orden);
    }

    @GetMapping("/{ordenId}/boleta")
    public ResponseEntity<byte[]> descargarBoleta(@PathVariable Long ordenId) {
        byte[] pdf = ordenService.generarBoletaPDF(ordenId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename("boleta_" + ordenId + ".pdf")
                        .build()
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdf);
    }
}
