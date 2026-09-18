package com.visit.jw_ls_maps_visit.controller;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visit.jw_ls_maps_visit.model.ContaUsuario;
import com.visit.jw_ls_maps_visit.repository.ContaUsuarioRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;

@RestController 
@RequestMapping("/api/users")
public class UsuarioController {
    
    private final ContaUsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final CurrentUser currentUser;

    public UsuarioController(ContaUsuarioRepository usuarioRepo, PasswordEncoder pass, CurrentUser user) {
        usuarioRepository = usuarioRepo;
        encoder = pass;
        currentUser = user;
    }

    @GetMapping("/me")
    public ContaUsuario me() {
        return currentUser.get();
    }

    @PutMapping("/save")
    public ContaUsuario updateMe(@RequestBody ContaUsuario usuario) {
        var me = currentUser.get();

        // Atualiza somente os campos permitidos do próprio perfil. Nunca salva
        // diretamente o objeto recebido, pois requisições parciais podem trazer
        // email (campo NOT NULL) e outros dados obrigatórios como null.
        if (usuario.getNomePublicador() != null) me.setNomePublicador(usuario.getNomePublicador());
        if (usuario.getCidade() != null) me.setCidade(usuario.getCidade());
        if (usuario.getEstado() != null) me.setEstado(usuario.getEstado());
        if (usuario.getManualAcesso() != null) me.setManualAcesso(usuario.getManualAcesso());
        if (usuario.getPendingReversalJson() != null) me.setPendingReversalJson(usuario.getPendingReversalJson());

        return usuarioRepository.save(me);
    }

    @GetMapping("/listAll")
    public List<ContaUsuario> list() {
        if (currentUser.is("admin"))
            return usuarioRepository.findAll();
        if (currentUser.is("superintendente"))
            return usuarioRepository.findByCircuitoId(currentUser.get().getCircuitoId());
            return usuarioRepository.findByCongregation(currentUser.get().getCongregation());
    }

    @GetMapping("/congregacao")
    public List<ContaUsuario> congregacao() {
        return usuarioRepository.findByCongregation(currentUser.get().getCongregation());
    }

    @GetMapping("/findById/{id}")
    public ContaUsuario getById(@PathVariable UUID id) {
        return usuarioRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public ContaUsuario create(@RequestBody ContaUsuario usuario) {
        if (!currentUser.isAny("admin", "elder", "superintendente")) throw new SecurityException("Sem permissão");
        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("Senha obrigatória");
        }
        usuario.setPasswordHash(encoder.encode(usuario.getPasswordHash()));
        if (usuario.getRole() == null || usuario.getRole().isBlank()) {
            usuario.setRole("publisher");
        }
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    @PutMapping("/update/{id}")
    public ContaUsuario update(@PathVariable UUID id, @RequestBody ContaUsuario usuario) {
        var target = usuarioRepository.findById(id).orElseThrow();
        var caller = currentUser.get();

        if (!caller.getRole().equalsIgnoreCase("admin")) {
            if (caller.getRole().equalsIgnoreCase("elder") && !Objects.equals(caller.getCongregation(), target.getCongregation())) throw new SecurityException("Usuário fora da congregação");
            if (caller.getRole().equalsIgnoreCase("superintendente") && !Objects.equals(caller.getCircuitoId(), target.getCircuitoId())) throw new SecurityException("Usuário fora do circuito");
            if (usuario.getRole() != null && (usuario.getRole().equalsIgnoreCase("admin") || usuario.getRole().equalsIgnoreCase("superintendente")) && caller.getRole().equalsIgnoreCase("elder")) throw new SecurityException("Não pode promover");
        }

        // PATCH semântico sobre a entidade já persistida. O formulário envia
        // apenas os campos alterados e normalmente não envia email/senha. Salvar
        // `usuario` diretamente zerava o email e violava @Column(nullable=false).
        if (usuario.getEmail() != null && !usuario.getEmail().isBlank()) target.setEmail(usuario.getEmail().trim());
        if (usuario.getRole() != null && !usuario.getRole().isBlank()) target.setRole(usuario.getRole());
        if (usuario.getCongregation() != null) target.setCongregation(usuario.getCongregation());
        if (usuario.getCongregationId() != null || usuario.getCongregation() != null) target.setCongregationId(usuario.getCongregationId());
        if (usuario.getCircuitoId() != null) target.setCircuitoId(usuario.getCircuitoId());
        if (usuario.getNomePublicador() != null) target.setNomePublicador(usuario.getNomePublicador());
        if (usuario.getCidade() != null) target.setCidade(usuario.getCidade());
        if (usuario.getEstado() != null) target.setEstado(usuario.getEstado());
        if (usuario.getManualAcesso() != null) target.setManualAcesso(usuario.getManualAcesso());
        if (usuario.getAtivo() != null) target.setAtivo(usuario.getAtivo());
        if (usuario.getPendingReversalJson() != null) target.setPendingReversalJson(usuario.getPendingReversalJson());
        if (usuario.getPermissao() != null) target.setPermissao(new java.util.HashSet<>(usuario.getPermissao()));

        if (usuario.getPasswordHash() != null && !usuario.getPasswordHash().isBlank()) {
            target.setPasswordHash(usuario.getPasswordHash().startsWith("$2")
                    ? usuario.getPasswordHash()
                    : encoder.encode(usuario.getPasswordHash()));
        }

        return usuarioRepository.save(target);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        if (!currentUser.is("admin")) throw new SecurityException("Somente administrador");
        usuarioRepository.deleteById(id);
    }

    @PostMapping("/invite")
    public ContaUsuario invite(@RequestBody ContaUsuario usuario) {
        return create(usuario);
    }

}
