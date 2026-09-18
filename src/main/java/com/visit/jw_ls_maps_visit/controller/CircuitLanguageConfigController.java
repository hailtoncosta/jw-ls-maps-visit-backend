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

@RestController 
@RequestMapping("/api/circuit-language-configs")
public class CircuitLanguageConfigController {
    
    private final CircuitLanguageConfigRepository circuitRepository;

    public CircuitLanguageConfigController(CircuitLanguageConfigRepository circuitoRepo) {
        circuitRepository = circuitoRepo;
    }

    @GetMapping("/listAll")
    public List<CircuitLanguageConfig> list() {
        return circuitRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public CircuitLanguageConfig getById(@PathVariable  UUID id) {
        return circuitRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public CircuitLanguageConfig create(@RequestBody CircuitLanguageConfig circuit) {
        return circuitRepository.save(circuit);
    }

    @PutMapping("/update/{id}")
    public CircuitLanguageConfig update(@PathVariable UUID id, @RequestBody CircuitLanguageConfig circuit) {
        circuit.setId(id);
        return circuitRepository.save(circuit);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        circuitRepository.deleteById(id);
    }

}
