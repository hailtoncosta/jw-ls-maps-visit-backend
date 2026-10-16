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
        validate(circuit);
        return circuitRepository.save(circuit);
    }

    @PutMapping("/update/{id}")
    public CircuitLanguageConfig update(@PathVariable UUID id, @RequestBody CircuitLanguageConfig circuit) {
        requireCircuit(getById(id));
        requireCircuit(circuit);
        circuit.setId(id);
        validate(circuit);
        return circuitRepository.save(circuit);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        requireCircuit(getById(id));
        circuitRepository.deleteById(id);
    }


    private void validate(CircuitLanguageConfig config) {
        if (config.getCircuitoId() == null) throw new IllegalArgumentException("Selecione o circuito.");
        if (config.getIdioma() == null || config.getIdioma().isBlank())
            throw new IllegalArgumentException("Selecione o idioma do circuito.");
        if (config.getSpeakers() == null) config.setSpeakers(0d);
        if (config.getPopulationRef() == null) config.setPopulationRef(0d);
        if (!Double.isFinite(config.getSpeakers()) || config.getSpeakers() < 0
                || !Double.isFinite(config.getPopulationRef()) || config.getPopulationRef() < 0)
            throw new IllegalArgumentException("Falantes e A cada (habitantes) devem ser números maiores ou iguais a zero.");
        config.setIdioma(config.getIdioma().trim());
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
