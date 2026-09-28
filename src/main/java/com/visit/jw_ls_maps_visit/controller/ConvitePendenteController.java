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
import com.visit.jw_ls_maps_visit.security.CurrentUser;

@RestController 
@RequestMapping("/api/convite-pendente")
public class ConvitePendenteController {

    private final ConvitePendenteRepository convitePendenteRepository;
    private final CurrentUser currentUser;

    public ConvitePendenteController(ConvitePendenteRepository conviteRepo, CurrentUser currentUser) {
        this.currentUser = currentUser;
        convitePendenteRepository = conviteRepo;
    }

    @GetMapping("/listAll")
    public List<ConvitePendente> list() {
        return convitePendenteRepository.findAll().stream().filter(this::inCircuit).toList();
    }

    @GetMapping("/findById/{id}")
    public ConvitePendente getById(@PathVariable UUID id) {
        var item = convitePendenteRepository.findById(id).orElseThrow();
        requireCircuit(item);
        return item;
    }

    @PostMapping("/save")
    public ConvitePendente save(@RequestBody ConvitePendente convite) {
        requireCircuit(convite);
        return convitePendenteRepository.save(convite);
    }

    @PutMapping("/update/{id}")
    public ConvitePendente update(@PathVariable UUID id, @RequestBody ConvitePendente convite) {
        requireCircuit(getById(id));
        requireCircuit(convite);
        convite.setId(id);
        return convitePendenteRepository.save(convite);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        requireCircuit(getById(id));
        convitePendenteRepository.deleteById(id);
    }
    

    private boolean inCircuit(ConvitePendente item) {
        if (!currentUser.is("superintendente")) return true;
        var circuit = currentUser.get().getCircuitoId();
        return circuit != null && circuit.equals(item.getCircuitoId());
    }

    private void requireCircuit(ConvitePendente item) {
        if (!inCircuit(item)) throw new SecurityException("Registro fora do circuito do superintendente");
    }
}
