package com.vivitasol.projectbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vivitasol.projectbackend.dto.AuthRequest;
import com.vivitasol.projectbackend.dto.AuthResponse;
import com.vivitasol.projectbackend.entities.Usuario;
import com.vivitasol.projectbackend.services.UsuarioServices;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioServices usuarioServices;

    // ==================== LOGIN ====================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            Usuario u = usuarioServices.login(req.getEmail(), req.getPassword());

            return ResponseEntity.ok(
                new AuthResponse(
                    u.getId(),
                    u.getNombre(),
                    u.getEmail(),
                    "Login exitoso"
                )
            );

        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, null, null, ex.getMessage()));
        }
    }

    // ==================== REGISTRO ====================
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario nuevo = usuarioServices.registrar(usuario);

            return ResponseEntity.ok(
                new AuthResponse(
                    nuevo.getId(),
                    nuevo.getNombre(),
                    nuevo.getEmail(),
                    "Usuario registrado correctamente"
                )
            );

        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, null, null, ex.getMessage()));
        }
    }
}
