package com.visit.jw_ls_maps_visit.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.visit.jw_ls_maps_visit.model.ContaUsuario;
import com.visit.jw_ls_maps_visit.repository.ContaUsuarioRepository;

@Component
public class CurrentUser {

    private final ContaUsuarioRepository contaUsuarioRepository;

    public CurrentUser(ContaUsuarioRepository contaUsuarioRepository) {
        this.contaUsuarioRepository = contaUsuarioRepository;
    }

    public ContaUsuario get() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
            !authentication.isAuthenticated()) {

            throw new SecurityException("Usuário não autenticado!");
        }

        String email = authentication.getName();

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        }

        if (email == null || email.isBlank()) {
            throw new SecurityException(
                    "Usuário autenticado sem e-mail!");
        }

        final String emailFinal = email;

        return contaUsuarioRepository
                .findByEmailIgnoreCase(emailFinal)
                .orElseThrow(() ->
                        new SecurityException(
                                "Usuário autenticado não encontrado: "
                                + emailFinal));
    }

    public boolean is(String role) {
        if (role == null) {
            return false;
        }

        return role.equalsIgnoreCase(get().getRole());
    }

    public boolean isAny(String... roles) {

        if (roles == null) {
            return false;
        }

        for (String role : roles) {
            if (is(role)) {
                return true;
            }
        }

        return false;
    }

    public boolean isAdmin() {
        return is("admin");
    }
}