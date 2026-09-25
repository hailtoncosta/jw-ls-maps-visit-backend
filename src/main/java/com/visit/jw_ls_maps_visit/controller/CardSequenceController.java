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
import com.visit.jw_ls_maps_visit.repository.CongregacaoRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;

@RestController 
@RequestMapping("/api/card-sequence")
public class CardSequenceController {

    private final CardSequenceRepository cardSequenceRepository;
    private final CongregacaoRepository congregacoes;
    private final CurrentUser currentUser;

    public CardSequenceController(CardSequenceRepository cardSequence, CongregacaoRepository congregacoes, CurrentUser currentUser) {
        cardSequenceRepository = cardSequence;
        this.congregacoes = congregacoes;
        this.currentUser = currentUser;
    }

    @GetMapping("/listAll")
    public List<CardSequence> list() {
        return cardSequenceRepository.findAll().stream().filter(this::inCircuit).toList();
    }

    @GetMapping("/findById/{id}")
    public CardSequence getById(@PathVariable  UUID id) {
        var card = cardSequenceRepository.findById(id).orElseThrow();
        requireCircuit(card);
        return card;
    }

    @PostMapping("/save")
    public CardSequence create(@RequestBody CardSequence card) {
        requireCircuit(card);
        return cardSequenceRepository.save(card);
    }

    @PutMapping("/update/{id}")
    public CardSequence update(@PathVariable UUID id, @RequestBody CardSequence card) {
        requireCircuit(getById(id));
        requireCircuit(card);
        card.setId(id);
        return cardSequenceRepository.save(card);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        requireCircuit(getById(id));
        cardSequenceRepository.deleteById(id);
    }

    private boolean inCircuit(CardSequence card) {
        if (!currentUser.is("superintendente")) return true;
        var circuitId = currentUser.get().getCircuitoId();
        return circuitId != null && congregacoes.findAll().stream()
            .anyMatch(cong -> circuitId.equals(cong.getCircuitoId()) && cong.getNome().equals(card.getCongregacao()));
    }

    private void requireCircuit(CardSequence card) {
        if (!inCircuit(card)) throw new SecurityException("Cartão fora do circuito do superintendente");
    }
    
    
}
