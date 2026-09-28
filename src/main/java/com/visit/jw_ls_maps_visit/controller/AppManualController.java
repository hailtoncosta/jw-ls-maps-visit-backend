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

import com.visit.jw_ls_maps_visit.model.AppManual;
import com.visit.jw_ls_maps_visit.repository.AppManualRepository;

@RestController 
@RequestMapping("/api/app-manuals")
public class AppManualController {
    
    private final AppManualRepository appRepository;

    public AppManualController(AppManualRepository appRepo) {
        appRepository = appRepo;
    }

    @GetMapping("/listAll")
    public List<AppManual> list() {
        return  appRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public AppManual get(@PathVariable UUID id) {
        return appRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public AppManual create(@RequestBody AppManual app) {
        return appRepository.save(app);
    }

    @PutMapping("/update/{id}")
    public AppManual update(@PathVariable UUID id, @RequestBody AppManual app) {
        app.setId(id);
        return appRepository.save(app);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable  UUID id) {
        appRepository.deleteById(id);
    }




}
