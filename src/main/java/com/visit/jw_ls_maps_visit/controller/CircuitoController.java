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

    public CircuitoController(CircuitoRepository circuitoRepo, CurrentUser user) {
        circuitoRepository = circuitoRepo;
        currentUser = user;
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
    public void delete(@PathVariable UUID id) {
        if (currentUser.is("superintendente")) throw new SecurityException("Exclusão de circuito requer administrador global");
        circuitoRepository.deleteById(id);
    }

    private void requireOwnCircuit(UUID id) {
        if (currentUser.is("superintendente")
                && (currentUser.get().getCircuitoId() == null || !currentUser.get().getCircuitoId().equals(id)))
            throw new SecurityException("Circuito fora do alcance do superintendente");
    }

}
