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

import com.visit.jw_ls_maps_visit.model.HistoricoVisitas;
import com.visit.jw_ls_maps_visit.repository.HistoricoVisitasRepository;

@RestController 
@RequestMapping("/api/historico-visitas")
public class HistoricoVisitasController {
    
    private final HistoricoVisitasRepository visitasRepository;

    private HistoricoVisitasController(HistoricoVisitasRepository historicoVisitasRepository) {
        visitasRepository = historicoVisitasRepository;
    }

    @GetMapping("/listAll")
    public List<HistoricoVisitas> list() {
        return visitasRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public HistoricoVisitas getById(@PathVariable UUID id) {
        return visitasRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public HistoricoVisitas create(@RequestBody HistoricoVisitas visitas) {
        return visitasRepository.save(visitas);
    }

    @PutMapping("/update/{id}")
    public HistoricoVisitas update(@PathVariable UUID id, @RequestBody HistoricoVisitas visitas) {
        visitas.setId(id);
        return visitasRepository.save(visitas);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        visitasRepository.deleteById(id);
    }

}
