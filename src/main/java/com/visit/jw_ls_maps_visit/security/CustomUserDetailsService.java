package com.visit.jw_ls_maps_visit.security;

import com.visit.jw_ls_maps_visit.model.ContaUsuario;
import com.visit.jw_ls_maps_visit.repository.ContaUsuarioRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final ContaUsuarioRepository usuarioRepository;

    public CustomUserDetailsService(
            ContaUsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        ContaUsuario usuario = usuarioRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "Usuário não encontrado: " + email
                    )
                );

        String role = usuario.getRole();
        if (role == null || role.isBlank()) {
            role = "publisher";
        }

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPasswordHash())
                .authorities("ROLE_" + role.toLowerCase())
                .disabled(!Boolean.TRUE.equals(usuario.getAtivo()))
                .build();
    }
}


