package com.visit.jw_ls_maps_visit.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.visit.jw_ls_maps_visit.model.PasswordResetToken;
import com.visit.jw_ls_maps_visit.repository.ContaUsuarioRepository;
import com.visit.jw_ls_maps_visit.repository.PasswordResetTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class PasswordResetService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final ContaUsuarioRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder encoder;
    private final ObjectProvider<JavaMailSender> mailProvider;

    @Value("${app.password-reset.enabled:false}") private boolean enabled;
    @Value("${app.password-reset.frontend-url:http://localhost:5173}") private String frontendUrl;
    @Value("${app.password-reset.from:}") private String from;

    public PasswordResetService(ContaUsuarioRepository users, PasswordResetTokenRepository tokens,
            PasswordEncoder encoder, ObjectProvider<JavaMailSender> mailProvider) {
        this.users = users;
        this.tokens = tokens;
        this.encoder = encoder;
        this.mailProvider = mailProvider;
    }

    @Transactional
    public void request(String email) {
        if (!enabled || from.isBlank() || mailProvider.getIfAvailable() == null)
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Recuperação de senha indisponível. Configure o e-mail SMTP no servidor.");
        if (email == null || email.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe seu e-mail.");

        var account = users.findByEmailIgnoreCase(email.trim()).orElse(null);
        // Mesma resposta para contas inexistentes e existentes.
        if (account == null || !Boolean.TRUE.equals(account.getAtivo())) return;

        var existing = tokens.findByUserId(account.getId());
        Instant now = Instant.now();
        if (existing.isPresent() && existing.get().getCreatedAt().plus(Duration.ofMinutes(2)).isAfter(now)) return;
        existing.ifPresent(tokens::delete);
        tokens.flush();

        byte[] random = new byte[32];
        RANDOM.nextBytes(random);
        String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(random);
        var token = new PasswordResetToken();
        token.setUserId(account.getId());
        token.setTokenHash(hash(secret));
        token.setCreatedAt(now);
        token.setExpiresAt(now.plus(Duration.ofMinutes(30)));
        tokens.save(token);

        String baseUrl = frontendUrl.replaceAll("/+$", "");
        var message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(account.getEmail());
        message.setSubject("Recuperação de senha — LS Maps Visit");
        message.setText("Para definir uma nova senha, abra o link abaixo em até 30 minutos:\n\n"
            + baseUrl + "/reset-password?token=" + secret
            + "\n\nSe você não pediu esta alteração, ignore este e-mail.");
        try {
            mailProvider.getObject().send(message);
        } catch (MailException error) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Não foi possível enviar o e-mail de recuperação. Tente novamente mais tarde.", error);
        }
    }

    @Transactional
    public void reset(String secret, String password) {
        if (secret == null || !secret.matches("[A-Za-z0-9_-]{43}"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link inválido ou expirado.");
        if (password == null || password.length() < 8 || password.length() > 128)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha deve ter entre 8 e 128 caracteres.");

        var token = tokens.findByTokenHash(hash(secret))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link inválido ou expirado."));
        if (!token.getExpiresAt().isAfter(Instant.now())) {
            tokens.delete(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link inválido ou expirado.");
        }
        var account = users.findById(token.getUserId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link inválido ou expirado."));
        if (!Boolean.TRUE.equals(account.getAtivo()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link inválido ou expirado.");
        account.setPasswordHash(encoder.encode(password));
        users.save(account);
        tokens.delete(token);
    }

    private static String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException(error);
        }
    }
}
