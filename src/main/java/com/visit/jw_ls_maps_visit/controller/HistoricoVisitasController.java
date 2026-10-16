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

import com.visit.jw_ls_maps_visit.model.HistoricoVisitas;
import com.visit.jw_ls_maps_visit.repository.HistoricoVisitasRepository;
import com.visit.jw_ls_maps_visit.repository.EnderecoRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;
import com.visit.jw_ls_maps_visit.security.UserPermissions;
import com.visit.jw_ls_maps_visit.service.EnderecoService;

@RestController 
@RequestMapping("/api/historico-visitas")
public class HistoricoVisitasController {
    
    private final HistoricoVisitasRepository visitasRepository;
    private final EnderecoRepository enderecos;
    private final CurrentUser currentUser;
    private final EnderecoService enderecoService;
    private final UserPermissions permissions;

    public HistoricoVisitasController(HistoricoVisitasRepository historicoVisitasRepository, EnderecoRepository enderecos, CurrentUser currentUser, EnderecoService enderecoService, UserPermissions permissions) {
        visitasRepository = historicoVisitasRepository;
        this.enderecos = enderecos;
        this.currentUser = currentUser;
        this.enderecoService = enderecoService;
        this.permissions = permissions;
    }

    @GetMapping("/listAll")
    public List<HistoricoVisitas> list() {
        permissions.require(currentUser.get(), "action_view");
        return visitasRepository.findAll().stream().filter(this::inCircuit).toList();
    }

    @GetMapping("/findById/{id}")
    public HistoricoVisitas getById(@PathVariable UUID id) {
        permissions.require(currentUser.get(), "action_view");
        var visita = visitasRepository.findById(id).orElseThrow();
        requireCircuit(visita);
        return visita;
    }

    @PostMapping("/save")
    public HistoricoVisitas create(@RequestBody HistoricoVisitas visitas) {
        permissions.require(currentUser.get(), "action_edit");
        requireCircuit(visitas);
        return visitasRepository.save(visitas);
    }

    @PutMapping("/update/{id}")
    public HistoricoVisitas update(@PathVariable UUID id, @RequestBody HistoricoVisitas visitas) {
        permissions.require(currentUser.get(), "action_edit");
        requireCircuit(getById(id));
        requireCircuit(visitas);
        visitas.setId(id);
        return visitasRepository.save(visitas);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        permissions.require(currentUser.get(), "action_delete");
        requireCircuit(getById(id));
        visitasRepository.deleteById(id);
    }

    private boolean inCircuit(HistoricoVisitas visita) {
        if (!currentUser.is("superintendente")) return true;
        return visita.getAddressId() != null && enderecos.findById(visita.getAddressId())
            .map(enderecoService::inSuperintendentScope).orElse(false);
    }

    private void requireCircuit(HistoricoVisitas visita) {
        if (!inCircuit(visita)) throw new SecurityException("Visita fora do circuito do superintendente");
    }

}
