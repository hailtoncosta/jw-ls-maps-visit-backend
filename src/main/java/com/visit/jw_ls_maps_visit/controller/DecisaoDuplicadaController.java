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

import com.visit.jw_ls_maps_visit.model.DecisaoDuplicada;
import com.visit.jw_ls_maps_visit.repository.DecisaoDuplicadaRepository;

@RestController 
@RequestMapping("/api/decisoes-duplicadas")
public class DecisaoDuplicadaController {
    
    private final DecisaoDuplicadaRepository decisaoDuplicadaRepository;

    public DecisaoDuplicadaController(DecisaoDuplicadaRepository decisaoRepo) {
        decisaoDuplicadaRepository = decisaoRepo;
    }

    @GetMapping("/listAll")
    public List<DecisaoDuplicada> list(){
        return decisaoDuplicadaRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public DecisaoDuplicada getById(@PathVariable UUID id) {
        return decisaoDuplicadaRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public DecisaoDuplicada create(@RequestBody DecisaoDuplicada decisaoRepo) {
        return decisaoDuplicadaRepository.save(decisaoRepo);
    }

    @PutMapping("/update/{id}")
    public DecisaoDuplicada update(@PathVariable UUID id, @RequestBody DecisaoDuplicada decisaoRepo) {
        decisaoRepo.setId(id);
        return decisaoDuplicadaRepository.save(decisaoRepo);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        decisaoDuplicadaRepository.deleteById(id);
    }

}
