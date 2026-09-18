package com.visit.jw_ls_maps_visit.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visit.jw_ls_maps_visit.model.Endereco;
import com.visit.jw_ls_maps_visit.repository.EnderecoRepository;
import com.visit.jw_ls_maps_visit.repository.HistoricoVisitasRepository;
import com.visit.jw_ls_maps_visit.service.EnderecoService;

@RestController 
@RequestMapping("/api/enderecos")
public class EnderecoController {
    
    private final EnderecoService enderecoService;
    private final HistoricoVisitasRepository historicoVisitasRepository;

    public EnderecoController (EnderecoService endService, EnderecoRepository endRepository, HistoricoVisitasRepository historico) {
        enderecoService = endService;
        historicoVisitasRepository = historico;
    }

    @GetMapping("/listAll")
    public List<Endereco> listEndereco() {
        return enderecoService.scoped();
    }

    @GetMapping("/scoped")
    public List<Endereco> scoped(){
        return enderecoService.scoped();
    }

    @GetMapping("/{id}")
    public Endereco get(@PathVariable UUID id) {
        return enderecoService.get(id);
    }

    @PostMapping("/save")
    public Endereco create(@RequestBody Endereco end) {
        return enderecoService.save(end);
    }

    @PutMapping("/update/{id}")
    public Endereco update(@PathVariable  UUID id, @RequestBody Endereco end){
        var endereco = enderecoService.get(id);
        enderecoService.checkEdit(endereco);
        end.setId(id);
        end.setCreatedById(endereco.getCreatedById());
        return enderecoService.save(end);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable  UUID id) {
        enderecoService.softDelete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/{id}/visitas")
    public List<?> historico(@PathVariable UUID id){
        return historicoVisitasRepository.findAll().stream().filter(v -> id.equals(v.getAddressId())).toList();
    }

    @GetMapping("/{id}/digital-card")
    public Endereco digitalCard(@PathVariable UUID id) {
        return enderecoService.get(id);
    }



}
