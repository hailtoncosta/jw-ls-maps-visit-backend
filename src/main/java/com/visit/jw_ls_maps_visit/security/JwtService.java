package com.visit.jw_ls_maps_visit.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiration;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expiration) {

        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expiration = expiration;
    }

    /**
     * Gera o token JWT.
     *
     * Subject = ID do usuário
     * email = e-mail do usuário
     * role = perfil do usuário
     */
    public String generate(UUID id, String email, String role) {

        Date now = new Date();
        Date expirationDate =
                new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(id.toString())
                .claim("email", email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    /**
     * Extrai o UUID do usuário através do subject.
     */
    public UUID id(String token) {

        Claims claims = getClaims(token);

        return UUID.fromString(
                claims.getSubject()
        );
    }

    /**
     * Extrai o e-mail do usuário.
     *
     * Como o subject contém o UUID,
     * o e-mail é armazenado no claim "email".
     */
    public String extractUsername(String token) {

        Claims claims = getClaims(token);

        return claims.get("email", String.class);
    }

    /**
     * Extrai a role do usuário.
     */
    public String extractRole(String token) {

        Claims claims = getClaims(token);

        return claims.get("role", String.class);
    }

    /**
     * Extrai a data de expiração.
     */
    public Date extractExpiration(String token) {

        Claims claims = getClaims(token);

        return claims.getExpiration();
    }

    /**
     * Verifica se o token é válido.
     *
     * O parser verifica:
     * - assinatura
     * - estrutura
     * - expiração
     */
    public boolean valid(String token) {

        try {

            getClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    /**
     * Valida o token comparando o username/e-mail.
     */
    public boolean isTokenValid(
            String token,
            String username) {

        try {

            String tokenUsername =
                    extractUsername(token);

            return tokenUsername != null
                    && tokenUsername.equals(username)
                    && !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }

    /**
     * Verifica se o token está expirado.
     */
    public boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    /**
     * Retorna todos os Claims do JWT.
     */
    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
