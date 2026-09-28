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

import com.visit.jw_ls_maps_visit.model.CircuitLanguageConfig;
import com.visit.jw_ls_maps_visit.repository.CircuitLanguageConfigRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;

@RestController 
@RequestMapping("/api/circuit-language-configs")
public class CircuitLanguageConfigController {
    
    private final CircuitLanguageConfigRepository circuitRepository;
    private final CurrentUser currentUser;

    public CircuitLanguageConfigController(CircuitLanguageConfigRepository circuitoRepo, CurrentUser currentUser) {
        this.currentUser = currentUser;
        circuitRepository = circuitoRepo;
    }

    @GetMapping("/listAll")
    public List<CircuitLanguageConfig> list() {
        return circuitRepository.findAll().stream().filter(this::inCircuit).toList();
    }

    @GetMapping("/findById/{id}")
    public CircuitLanguageConfig getById(@PathVariable  UUID id) {
        var item = circuitRepository.findById(id).orElseThrow();
        requireCircuit(item);
        return item;
    }

    @PostMapping("/save")
    public CircuitLanguageConfig create(@RequestBody CircuitLanguageConfig circuit) {
        requireCircuit(circuit);
        return circuitRepository.save(circuit);
    }

    @PutMapping("/update/{id}")
    public CircuitLanguageConfig update(@PathVariable UUID id, @RequestBody CircuitLanguageConfig circuit) {
        requireCircuit(getById(id));
        requireCircuit(circuit);
        circuit.setId(id);
        return circuitRepository.save(circuit);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        requireCircuit(getById(id));
        circuitRepository.deleteById(id);
    }


    private boolean inCircuit(CircuitLanguageConfig item) {
        if (!currentUser.is("superintendente")) return true;
        var circuit = currentUser.get().getCircuitoId();
        return circuit != null && circuit.equals(item.getCircuitoId());
    }

    private void requireCircuit(CircuitLanguageConfig item) {
        if (!inCircuit(item)) throw new SecurityException("Registro fora do circuito do superintendente");
    }
}
