package com.vivitasol.projectbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vivitasol.projectbackend.dto.AuthRequest;
import com.vivitasol.projectbackend.dto.AuthResponse;
import com.vivitasol.projectbackend.entities.Usuario;
import com.vivitasol.projectbackend.security.JwtUtil;
import com.vivitasol.projectbackend.services.UsuarioServices;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioServices usuarioServices;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==================== LOGIN ====================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            Usuario usuario = usuarioServices.obtenerPorEmail(req.getEmail());

            if (usuario == null) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, null, null, null, "Email no registrado"));
            }

            if (!passwordEncoder.matches(req.getPassword(), usuario.getPassword())) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, null, null, null, "Contraseña incorrecta"));
            }

            if (!usuario.getActivo()) {
                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new AuthResponse(null, null, null, null, null, "Usuario desactivado"));
            }

            // Generar token JWT
            String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getId(),
                usuario.getRol()
            );

            return ResponseEntity.ok(
                new AuthResponse(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getRol(),   // ✅ Enviar ROL
                    token,
                    "Login exitoso"
                )
            );

        } catch (Exception ex) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(null, null, null, null, null, "Error en el servidor"));
        }
    }

    // ==================== REGISTRO ====================
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

            if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
                usuario.setRol("CLIENTE");
            }

            Usuario nuevo = usuarioServices.registrar(usuario);

            String token = jwtUtil.generateToken(
                nuevo.getEmail(),
                nuevo.getId(),
                nuevo.getRol()
            );

            return ResponseEntity.ok(
                new AuthResponse(
                    nuevo.getId(),
                    nuevo.getNombre(),
                    nuevo.getEmail(),
                    nuevo.getRol(),   // ✅ Enviar ROL
                    token,
                    "Usuario registrado correctamente"
                )
            );

        } catch (RuntimeException ex) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new AuthResponse(null, null, null, null, null, ex.getMessage()));
        }
    }

    // ==================== REFRESH TOKEN ====================
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, null, null, null, null, "Token no proporcionado"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);

            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, null, null, null, "Token expirado"));
            }

            Usuario usuario = usuarioServices.obtenerPorEmail(email);
            if (usuario == null) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new AuthResponse(null, null, null, null, null, "Usuario no encontrado"));
            }

            String newToken = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getId(),
                usuario.getRol()
            );

            return ResponseEntity.ok(
                new AuthResponse(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getRol(),   // ✅ Enviar ROL también aquí
                    newToken,
                    "Token renovado"
                )
            );

        } catch (Exception ex) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(null, null, null, null, null, "Error al renovar token"));
        }
    }
}
