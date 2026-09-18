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

@RestController 
@RequestMapping("/api/circuitos")
public class CircuitoController {
    
    private final CircuitoRepository circuitoRepository;

    public CircuitoController(CircuitoRepository circuitoRepo) {
        circuitoRepository = circuitoRepo;
    }

    @GetMapping("/listAll")
    public List<Circuito> list() {
        return circuitoRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public Circuito getById(@PathVariable UUID id) {
        return circuitoRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public Circuito create(@RequestBody Circuito circ) {
        return circuitoRepository.save(circ);
    }

    @PutMapping("/update/{id}")
    public Circuito update(@PathVariable UUID id, @RequestBody Circuito circ) {
        circ.setId(id);
        return circuitoRepository.save(circ);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        circuitoRepository.deleteById(id);
    }

}
