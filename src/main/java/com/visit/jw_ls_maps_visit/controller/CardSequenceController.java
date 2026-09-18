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

import com.visit.jw_ls_maps_visit.model.CardSequence;
import com.visit.jw_ls_maps_visit.repository.CardSequenceRepository;

@RestController 
@RequestMapping("/api/card-sequence")
public class CardSequenceController {

    private final CardSequenceRepository cardSequenceRepository;

    public CardSequenceController(CardSequenceRepository cardSequence) {
        cardSequenceRepository = cardSequence;
    }

    @GetMapping("/listAll")
    public List<CardSequence> list() {
        return cardSequenceRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public CardSequence getById(@PathVariable  UUID id) {
        return cardSequenceRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public CardSequence create(@RequestBody CardSequence card) {
        return cardSequenceRepository.save(card);
    }

    @PutMapping("/update/{id}")
    public CardSequence update(@PathVariable UUID id, @RequestBody CardSequence card) {
        card.setId(id);
        return cardSequenceRepository.save(card);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        cardSequenceRepository.deleteById(id);
    }
    
    
}
