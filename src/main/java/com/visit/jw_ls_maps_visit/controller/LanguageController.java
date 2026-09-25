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

import com.visit.jw_ls_maps_visit.model.Language;
import com.visit.jw_ls_maps_visit.repository.LanguageRepository;

@RestController 
@RequestMapping("/api/languages")
public class LanguageController {
    
    private final LanguageRepository languageRepository;

    public LanguageController(LanguageRepository langRepo) {
        languageRepository = langRepo;
    }

    @GetMapping("/listAll")
    public List<Language> list() {
        return languageRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public Language getById(@PathVariable UUID id) {
        return languageRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public Language create(@RequestBody Language lang) {
        return languageRepository.save(lang);
    }

    @PutMapping("/update/{id}")
    public Language update(@PathVariable UUID id, @RequestBody Language lang) {
        lang.setId(id);
        return languageRepository.save(lang);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        languageRepository.deleteById(id);
    }

}
