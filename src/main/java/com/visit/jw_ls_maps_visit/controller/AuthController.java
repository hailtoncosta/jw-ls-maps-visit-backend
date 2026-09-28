package com.visit.jw_ls_maps_visit.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visit.jw_ls_maps_visit.model.ContaUsuario;
import com.visit.jw_ls_maps_visit.repository.ContaUsuarioRepository;
import com.visit.jw_ls_maps_visit.security.JwtService;
import com.visit.jw_ls_maps_visit.service.PasswordResetService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ContaUsuarioRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final PasswordResetService passwordReset;

    public AuthController(ContaUsuarioRepository users, PasswordEncoder encoder, JwtService jwt, PasswordResetService passwordReset) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.passwordReset = passwordReset;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        passwordReset.request(body.get("email"));
        return ResponseEntity.ok(Map.of("message", "Se este e-mail estiver cadastrado, você receberá um link de recuperação."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        passwordReset.reset(body.get("token"), body.get("password"));
        return ResponseEntity.ok(Map.of("message", "Senha alterada. Faça login com a nova senha."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "").trim().toLowerCase();
        String password = body.getOrDefault("password", "");

        if (email.isBlank() || password.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("error", "E-mail e senha são obrigatórios."));
        }

        ContaUsuario usuario = users.findByEmailIgnoreCase(email).orElse(null);

        if (usuario == null
                || !Boolean.TRUE.equals(usuario.getAtivo())
                || usuario.getPasswordHash() == null
                || !encoder.matches(password, usuario.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciais inválidas."));
        }

        String role = usuario.getRole() == null || usuario.getRole().isBlank()
                ? "publisher" : usuario.getRole().toLowerCase();

        String token = jwt.generate(usuario.getId(), usuario.getEmail(), role);

        // Nunca devolve password_hash ao frontend.
        Map<String, Object> safeUser = new LinkedHashMap<>();
        safeUser.put("id", usuario.getId());
        safeUser.put("email", usuario.getEmail());
        safeUser.put("role", role);
        safeUser.put("nome_publicador", usuario.getNomePublicador());
        safeUser.put("congregation", usuario.getCongregation());
        safeUser.put("congregation_id", usuario.getCongregationId());
        safeUser.put("circuito_id", usuario.getCircuitoId());
        safeUser.put("cidade", usuario.getCidade());
        safeUser.put("estado", usuario.getEstado());
        safeUser.put("manual_acesso", usuario.getManualAcesso());
        safeUser.put("ativo", usuario.getAtivo());
        safeUser.put("permissao", usuario.getPermissao());

        return ResponseEntity.ok(Map.of("token", token, "user", safeUser));
    }
}
