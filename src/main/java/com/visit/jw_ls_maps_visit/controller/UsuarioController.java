package com.visit.jw_ls_maps_visit.controller;

import java.util.List;
import java.util.Map;
import java.util.Comparator;
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

import com.visit.jw_ls_maps_visit.dto.InviteUserRequest;
import com.visit.jw_ls_maps_visit.model.ContaUsuario;
import com.visit.jw_ls_maps_visit.repository.ContaUsuarioRepository;
import com.visit.jw_ls_maps_visit.repository.PasswordResetTokenRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;
import jakarta.transaction.Transactional;

@RestController 
@RequestMapping("/api/users")
public class UsuarioController {
    
    private final ContaUsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final CurrentUser currentUser;
    private final PasswordResetTokenRepository resetTokens;

    public UsuarioController(ContaUsuarioRepository usuarioRepo, PasswordEncoder pass, CurrentUser user,
            PasswordResetTokenRepository resetTokens) {
        usuarioRepository = usuarioRepo;
        encoder = pass;
        currentUser = user;
        this.resetTokens = resetTokens;
    }

    @GetMapping("/me")
    public ContaUsuario me() {
        return currentUser.get();
    }

    @PostMapping("/change-password")
    @Transactional
    public Map<String, String> changePassword(@RequestBody Map<String, String> body) {
        var user = currentUser.get();
        String current = body.get("current_password");
        String next = body.get("new_password");
        if (current == null || user.getPasswordHash() == null || !encoder.matches(current, user.getPasswordHash()))
            throw new IllegalArgumentException("Senha atual incorreta.");
        if (next == null || next.length() < 8 || next.length() > 128)
            throw new IllegalArgumentException("A nova senha deve ter entre 8 e 128 caracteres.");
        if (encoder.matches(next, user.getPasswordHash()))
            throw new IllegalArgumentException("Escolha uma senha diferente da atual.");
        user.setPasswordHash(encoder.encode(next));
        usuarioRepository.save(user);
        resetTokens.findByUserId(user.getId()).ifPresent(resetTokens::delete);
        return Map.of("message", "Senha alterada com sucesso.");
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
            return currentUser.get().getCircuitoId() == null ? List.of() : usuarioRepository.findByCircuitoId(currentUser.get().getCircuitoId());
            return usuarioRepository.findByCongregation(currentUser.get().getCongregation());
    }

    @GetMapping("/congregacao")
    public List<ContaUsuario> congregacao() {
        var caller = currentUser.get();
        if ("admin".equalsIgnoreCase(caller.getRole())) {
            return usuarioRepository.findAll();
        }
        if ("superintendente".equalsIgnoreCase(caller.getRole())) {
            return caller.getCircuitoId() == null ? List.of() : usuarioRepository.findByCircuitoId(caller.getCircuitoId());
        }
        return usuarioRepository.findByCongregation(caller.getCongregation());
    }

    @GetMapping("/share-candidates")
    public List<Map<String, String>> shareCandidates() {
        var callerId = currentUser.get().getId();
        return usuarioRepository.findAll().stream()
            .filter(user -> Boolean.TRUE.equals(user.getAtivo()) && !user.getId().equals(callerId))
            .filter(user -> !currentUser.is("superintendente") || currentUser.get().getCircuitoId() != null
                && currentUser.get().getCircuitoId().equals(user.getCircuitoId()))
            .sorted(Comparator.comparing(user -> user.getNomePublicador() == null ? user.getEmail() : user.getNomePublicador(), String.CASE_INSENSITIVE_ORDER))
            .map(user -> Map.of(
                "id", user.getId().toString(),
                "full_name", user.getNomePublicador() == null ? "" : user.getNomePublicador(),
                "email", user.getEmail(),
                "congregation", user.getCongregation() == null ? "" : user.getCongregation()
            ))
            .toList();
    }

    @GetMapping("/findById/{id}")
    public ContaUsuario getById(@PathVariable UUID id) {
        var target = usuarioRepository.findById(id).orElseThrow();
        requireCircuit(target);
        return target;
    }

    @PostMapping("/save")
    public ContaUsuario create(@RequestBody ContaUsuario usuario) {
        if (!currentUser.isAny("admin", "elder", "superintendente")) throw new SecurityException("Sem permissão");
        requireCircuit(usuario);
        if (currentUser.is("superintendente") && "admin".equalsIgnoreCase(usuario.getRole()))
            throw new SecurityException("Não é permitido criar administrador global");
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
        requireCircuit(target);
        if ("superintendente".equalsIgnoreCase(caller.getRole())) {
            if (usuario.getCircuitoId() != null && !usuario.getCircuitoId().equals(caller.getCircuitoId()))
                throw new SecurityException("Não é permitido mover usuário para outro circuito");
            if ("admin".equalsIgnoreCase(usuario.getRole()))
                throw new SecurityException("Não é permitido atribuir administrador global");
        }

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
        if (!currentUser.isAny("admin", "superintendente")) throw new SecurityException("Sem permissão");
        requireCircuit(usuarioRepository.findById(id).orElseThrow());
        usuarioRepository.deleteById(id);
    }

    @PostMapping("/invite")
    public Map<String, Object> invite(@RequestBody InviteUserRequest request) {
        var caller = currentUser.get();
        if (!currentUser.isAny("admin", "elder", "superintendente")) {
            throw new SecurityException("Sem permissão para convidar usuários");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("E-mail obrigatório");
        }

        String email = request.email().trim().toLowerCase();
        String requestedRole = request.role() == null || request.role().isBlank()
                ? "publisher"
                : request.role().trim().toLowerCase();

        if (!caller.getRole().equalsIgnoreCase("admin")) {
            if ("admin".equals(requestedRole)) {
                throw new SecurityException("Somente administrador pode atribuir o perfil de administrador");
            }
            if (caller.getRole().equalsIgnoreCase("elder")
                    && !Objects.equals(caller.getCongregation(), request.congregation())) {
                throw new SecurityException("A congregação deve ser a mesma do administrador local");
            }
            if (caller.getRole().equalsIgnoreCase("superintendente")
                    && !Objects.equals(caller.getCircuitoId(), request.circuitoId())) {
                throw new SecurityException("O circuito deve ser o mesmo do superintendente");
            }
        }

        var existing = usuarioRepository.findByEmailIgnoreCase(email);
        existing.ifPresent(this::requireCircuit);
        boolean created = existing.isEmpty();
        var usuario = existing.orElseGet(ContaUsuario::new);

        if (created) {
            if (request.password() == null || request.password().length() < 8) {
                throw new IllegalArgumentException("A senha inicial deve ter pelo menos 8 caracteres");
            }
            usuario.setEmail(email);
            usuario.setPasswordHash(encoder.encode(request.password()));
        } else if (request.password() != null && !request.password().isBlank()) {
            if (request.password().length() < 8) {
                throw new IllegalArgumentException("A nova senha deve ter pelo menos 8 caracteres");
            }
            usuario.setPasswordHash(encoder.encode(request.password()));
        }

        usuario.setRole(requestedRole);
        usuario.setCongregation(request.congregation() == null ? "" : request.congregation());
        usuario.setCongregationId(request.congregationId());
        usuario.setCircuitoId(request.circuitoId());
        if (request.fullName() != null) usuario.setNomePublicador(request.fullName());
        if (request.manualAccess() != null) usuario.setManualAcesso(request.manualAccess());
        usuario.setAtivo(request.active() == null || request.active());
        usuario.setPermissao(request.permissions() == null
                ? new java.util.HashSet<>()
                : new java.util.HashSet<>(request.permissions()));

        var saved = usuarioRepository.save(usuario);
        return Map.of("action", created ? "created" : "updated", "user", saved);
    }

    private void requireCircuit(ContaUsuario target) {
        if (currentUser.is("superintendente")
                && (currentUser.get().getCircuitoId() == null
                    || !currentUser.get().getCircuitoId().equals(target.getCircuitoId())
                    || "admin".equalsIgnoreCase(target.getRole())))
            throw new SecurityException("Usuário fora do circuito do superintendente");
    }

}
