package com.visit.jw_ls_maps_visit.controller;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.visit.jw_ls_maps_visit.model.Congregacao;
import com.visit.jw_ls_maps_visit.model.ContaUsuario;
import com.visit.jw_ls_maps_visit.repository.CongregacaoRepository;
import com.visit.jw_ls_maps_visit.repository.ContaUsuarioRepository;
import com.visit.jw_ls_maps_visit.repository.EnderecoRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;

@RestController
@RequestMapping("/api/congregacoes")
public class CongregacaoController {

    private final CongregacaoRepository congregacaoRepository;
    private final EnderecoRepository enderecoRepository;
    private final ContaUsuarioRepository contaUsuarioRepository;
    private final CurrentUser currentUser;

    public CongregacaoController(
            CongregacaoRepository congregacaoRepository,
            EnderecoRepository enderecoRepository,
            ContaUsuarioRepository contaUsuarioRepository,
            CurrentUser currentUser) {
        this.congregacaoRepository = congregacaoRepository;
        this.enderecoRepository = enderecoRepository;
        this.contaUsuarioRepository = contaUsuarioRepository;
        this.currentUser = currentUser;
    }

    @GetMapping("/listAll")
    public List<Congregacao> list() {
        return congregacaoRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public Congregacao getById(@PathVariable UUID id) {
        return findOrThrow(id);
    }

    @PostMapping("/save")
    public Congregacao create(@RequestBody Congregacao congregacao) {
        requireManagePermission();
        validar(congregacao);
        validateCircuitScope(congregacao.getCircuitoId());
        congregacao.setId(null);
        return congregacaoRepository.save(congregacao);
    }

    @PutMapping("/update/{id}")
    public Congregacao update(@PathVariable UUID id, @RequestBody Congregacao dados) {
        requireManagePermission();
        validar(dados);
        validateCircuitScope(dados.getCircuitoId());

        Congregacao atual = findOrThrow(id);
        validateCircuitScope(atual.getCircuitoId());

        String nomeAnterior = atual.getNome();
        atual.setNome(dados.getNome().trim());
        atual.setCircuito(dados.getCircuito());
        atual.setCircuitoId(dados.getCircuitoId());
        Congregacao salva = congregacaoRepository.save(atual);

        // Mantém endereços e usuários sincronizados quando nome/circuito mudam.
        for (var endereco : enderecoRepository.findAll()) {
            if (Objects.equals(endereco.getCongregacaoId(), id)
                    || Objects.equals(endereco.getCongregacao(), nomeAnterior)) {
                endereco.setCongregacaoId(id);
                endereco.setCongregacao(salva.getNome());
                endereco.setCircuitoId(salva.getCircuitoId());
                enderecoRepository.save(endereco);
            }
        }

        for (var usuario : contaUsuarioRepository.findAll()) {
            if (Objects.equals(usuario.getCongregationId(), id)
                    || Objects.equals(usuario.getCongregation(), nomeAnterior)) {
                usuario.setCongregationId(id);
                usuario.setCongregation(salva.getNome());
                usuario.setCircuitoId(salva.getCircuitoId());
                contaUsuarioRepository.save(usuario);
            }
        }

        return salva;
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        requireManagePermission();
        Congregacao atual = findOrThrow(id);
        validateCircuitScope(atual.getCircuitoId());
        congregacaoRepository.delete(atual);
    }

    private Congregacao findOrThrow(UUID id) {
        return congregacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Congregação não encontrada"));
    }

    private void validar(Congregacao congregacao) {
        if (congregacao.getNome() == null || congregacao.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome da congregação é obrigatório");
        }
        if (congregacao.getCircuitoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O circuito é obrigatório");
        }
        congregacao.setNome(congregacao.getNome().trim());
    }

    private void requireManagePermission() {
        ContaUsuario user = currentUser.get();
        boolean roleAllowed = "admin".equalsIgnoreCase(user.getRole())
                || "superintendente".equalsIgnoreCase(user.getRole());
        boolean explicitPermission = user.getPermissao() != null
                && user.getPermissao().contains("admin_manage_congregations");
        if (!roleAllowed && !explicitPermission) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissão para gerenciar congregações");
        }
    }

    private void validateCircuitScope(UUID circuitoId) {
        ContaUsuario user = currentUser.get();
        if ("superintendente".equalsIgnoreCase(user.getRole())
                && !Objects.equals(user.getCircuitoId(), circuitoId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A congregação deve pertencer ao seu circuito");
        }
    }
}
