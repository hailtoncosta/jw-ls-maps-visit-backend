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
import com.visit.jw_ls_maps_visit.security.CurrentUser;

@RestController 
@RequestMapping("/api/language-gols")
public class LanguageGoalController {
    
    private final LanguageGoalRepository languageGoalRepository;
    private final CurrentUser currentUser;

    public LanguageGoalController(LanguageGoalRepository goalRepository, CurrentUser currentUser) {
        this.currentUser = currentUser;
        languageGoalRepository = goalRepository;
    }

    @GetMapping("/listAll")
    public List<LanguageGoal> list() {
        return languageGoalRepository.findAll().stream().filter(this::inCircuit).toList();
    }

    @GetMapping("/findById/{id}")
    public LanguageGoal getById(@PathVariable UUID id) {
        var item = languageGoalRepository.findById(id).orElseThrow();
        requireCircuit(item);
        return item;
    }

    @PostMapping("/save")
    public LanguageGoal create(@RequestBody LanguageGoal lang) {
        requireCircuit(lang);
        return languageGoalRepository.save(lang);
    }

    @PutMapping("/update/{id}")
    public LanguageGoal update(@PathVariable UUID id, @RequestBody LanguageGoal lang) {
        requireCircuit(getById(id));
        requireCircuit(lang);
        lang.setId(id);
        return languageGoalRepository.save(lang);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        requireCircuit(getById(id));
        languageGoalRepository.deleteById(id);
    }


    private boolean inCircuit(LanguageGoal item) {
        if (!currentUser.is("superintendente")) return true;
        var circuit = currentUser.get().getCircuitoId();
        return circuit != null && circuit.equals(item.getCircuitoId());
    }

    private void requireCircuit(LanguageGoal item) {
        if (!inCircuit(item)) throw new SecurityException("Registro fora do circuito do superintendente");
    }
}
