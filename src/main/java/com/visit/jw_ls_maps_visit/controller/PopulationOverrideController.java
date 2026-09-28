package com.visit.jw_ls_maps_visit.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.visit.jw_ls_maps_visit.model.PopulationOverride;
import com.visit.jw_ls_maps_visit.repository.PopulationOverrideRepository;

@RestController 
@RequestMapping("/api/population-overrides")
public class PopulationOverrideController {

    private final PopulationOverrideRepository populationOverrideRepository;

    public PopulationOverrideController(PopulationOverrideRepository overrideRepository) {
        populationOverrideRepository = overrideRepository;
    }

    @GetMapping("/listAll")
    public List<PopulationOverride> list() {
        return populationOverrideRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public PopulationOverride getById(@PathVariable UUID id) {
        return populationOverrideRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public PopulationOverride save(@RequestBody PopulationOverride population) {
        return populationOverrideRepository.save(population);
    }

    @PutMapping("/update/{id}")
    public PopulationOverride updante(@PathVariable UUID id, @RequestBody PopulationOverride population) {
        population.setId(id);
        return populationOverrideRepository.save(population);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        populationOverrideRepository.deleteById(id);
    }
    
}
