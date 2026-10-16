package com.visit.jw_ls_maps_visit.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visit.jw_ls_maps_visit.model.Circuito;
import com.visit.jw_ls_maps_visit.repository.CircuitoRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;

@RestController 
@RequestMapping("/api/circuitos")
public class CircuitoController {
    
    private final CircuitoRepository circuitoRepository;
    private final CurrentUser currentUser;
    private final com.visit.jw_ls_maps_visit.repository.CongregacaoRepository congregacoes;

    public CircuitoController(CircuitoRepository circuitoRepo, CurrentUser user,
            com.visit.jw_ls_maps_visit.repository.CongregacaoRepository congregacoes) {
        circuitoRepository = circuitoRepo;
        currentUser = user;
        this.congregacoes = congregacoes;
    }

    @GetMapping("/listAll")
    public List<Circuito> list() {
        if (currentUser.is("superintendente")) {
            var id = currentUser.get().getCircuitoId();
            return id == null ? List.of() : circuitoRepository.findById(id).stream().toList();
        }
        return circuitoRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public Circuito getById(@PathVariable UUID id) {
        requireOwnCircuit(id);
        return circuitoRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public Circuito create(@RequestBody Circuito circ) {
        if (currentUser.is("superintendente")) throw new SecurityException("Novo circuito requer administrador global");
        return circuitoRepository.save(circ);
    }

    @PutMapping("/update/{id}")
    public Circuito update(@PathVariable UUID id, @RequestBody Circuito circ) {
        requireOwnCircuit(id);
        circ.setId(id);
        return circuitoRepository.save(circ);
    }

    @DeleteMapping("/delete/{id}")
    @jakarta.transaction.Transactional
    public void delete(@PathVariable UUID id) {
        var caller = currentUser.get();
        if (!Boolean.TRUE.equals(caller.getAtivo()) || !"admin".equalsIgnoreCase(caller.getRole()))
            throw new org.springframework.security.access.AccessDeniedException("Exclusão de circuito requer administrador geral.");
        var circuito = circuitoRepository.findById(id).orElseThrow();
        boolean hasCongregations = congregacoes.findAll().stream().anyMatch(cong ->
            id.equals(cong.getCircuitoId()) || (cong.getCircuitoId() == null
                && cong.getCircuito() != null && circuito.getNome() != null
                && cong.getCircuito().trim().equalsIgnoreCase(circuito.getNome().trim())));
        if (hasCongregations)
            throw new IllegalArgumentException("Este circuito possui congregações cadastradas. Exclua as congregações antes de excluir o circuito.");
        circuitoRepository.deleteById(id);
    }

    private void requireOwnCircuit(UUID id) {
        if (currentUser.is("superintendente")
                && (currentUser.get().getCircuitoId() == null || !currentUser.get().getCircuitoId().equals(id)))
            throw new SecurityException("Circuito fora do alcance do superintendente");
    }

}
