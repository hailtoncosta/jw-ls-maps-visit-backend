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

import com.visit.jw_ls_maps_visit.model.ConvitePendente;
import com.visit.jw_ls_maps_visit.repository.ConvitePendenteRepository;

@RestController 
@RequestMapping("/api/convite-pendente")
public class ConvitePendenteController {

    private final ConvitePendenteRepository convitePendenteRepository;

    public ConvitePendenteController(ConvitePendenteRepository conviteRepo) {
        convitePendenteRepository = conviteRepo;
    }

    @GetMapping("/listAll")
    public List<ConvitePendente> list() {
        return convitePendenteRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public ConvitePendente getById(@PathVariable UUID id) {
        return convitePendenteRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public ConvitePendente save(@RequestBody ConvitePendente convite) {
        return convitePendenteRepository.save(convite);
    }

    @PutMapping("/update/{id}")
    public ConvitePendente update(@PathVariable UUID id, @RequestBody ConvitePendente convite) {
        convite.setId(id);
        return convitePendenteRepository.save(convite);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        convitePendenteRepository.deleteById(id);
    }
    
}
