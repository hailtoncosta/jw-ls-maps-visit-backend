package com.visit.jw_ls_maps_visit.controller;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

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
import com.visit.jw_ls_maps_visit.repository.EnderecoRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;

@RestController 
@RequestMapping("/api/historico-visitas")
public class HistoricoVisitasController {
    
    private final HistoricoVisitasRepository visitasRepository;
    private final EnderecoRepository enderecos;
    private final CurrentUser currentUser;

    public HistoricoVisitasController(HistoricoVisitasRepository historicoVisitasRepository, EnderecoRepository enderecos, CurrentUser currentUser) {
        visitasRepository = historicoVisitasRepository;
        this.enderecos = enderecos;
        this.currentUser = currentUser;
    }

    @GetMapping("/listAll")
    public List<HistoricoVisitas> list() {
        return visitasRepository.findAll().stream().filter(this::inCircuit).toList();
    }

    @GetMapping("/findById/{id}")
    public HistoricoVisitas getById(@PathVariable UUID id) {
        var visita = visitasRepository.findById(id).orElseThrow();
        requireCircuit(visita);
        return visita;
    }

    @PostMapping("/save")
    public HistoricoVisitas create(@RequestBody HistoricoVisitas visitas) {
        requireCircuit(visitas);
        return visitasRepository.save(visitas);
    }

    @PutMapping("/update/{id}")
    public HistoricoVisitas update(@PathVariable UUID id, @RequestBody HistoricoVisitas visitas) {
        requireCircuit(getById(id));
        requireCircuit(visitas);
        visitas.setId(id);
        return visitasRepository.save(visitas);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        requireCircuit(getById(id));
        visitasRepository.deleteById(id);
    }

    private boolean inCircuit(HistoricoVisitas visita) {
        if (!currentUser.is("superintendente")) return true;
        var circuitId = currentUser.get().getCircuitoId();
        return circuitId != null && visita.getAddressId() != null && enderecos.findById(visita.getAddressId())
            .map(endereco -> Objects.equals(circuitId, endereco.getCircuitoId())).orElse(false);
    }

    private void requireCircuit(HistoricoVisitas visita) {
        if (!inCircuit(visita)) throw new SecurityException("Visita fora do circuito do superintendente");
    }

}
