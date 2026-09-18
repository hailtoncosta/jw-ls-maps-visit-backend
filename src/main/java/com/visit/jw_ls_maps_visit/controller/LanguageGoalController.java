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

import com.visit.jw_ls_maps_visit.model.LanguageGoal;
import com.visit.jw_ls_maps_visit.repository.LanguageGoalRepository;

@RestController 
@RequestMapping("/api/language-gols")
public class LanguageGoalController {
    
    private final LanguageGoalRepository languageGoalRepository;

    public LanguageGoalController(LanguageGoalRepository goalRepository) {
        languageGoalRepository = goalRepository;
    }

    @GetMapping("/listAll")
    public List<LanguageGoal> list() {
        return languageGoalRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public LanguageGoal getById(@PathVariable UUID id) {
        return languageGoalRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public LanguageGoal create(@RequestBody LanguageGoal lang) {
        return languageGoalRepository.save(lang);
    }

    @PutMapping("/update/{id}")
    public LanguageGoal update(@PathVariable UUID id, @RequestBody LanguageGoal lang) {
        lang.setId(id);
        return languageGoalRepository.save(lang);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        languageGoalRepository.deleteById(id);
    }

}
