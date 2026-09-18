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

import com.visit.jw_ls_maps_visit.model.EnderecoCompartilhado;
import com.visit.jw_ls_maps_visit.repository.EnderecoCompartilhadoRepository;
import com.visit.jw_ls_maps_visit.service.EnderecoCompartilhadoService;

@RestController 
@RequestMapping("/api/endereco-compartilhado")
public class EnderecoCompartilhadoController {
    
    private final EnderecoCompartilhadoRepository enderecoRepository;
    private final EnderecoCompartilhadoService enderecoService;

    public EnderecoCompartilhadoController(EnderecoCompartilhadoRepository enderecoRepo, EnderecoCompartilhadoService endService) {
        enderecoRepository = enderecoRepo;
        enderecoService = endService;
    }

    @GetMapping("/listAll")
    public List<EnderecoCompartilhado> list() {
        return enderecoRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public EnderecoCompartilhado getById(@PathVariable UUID id) {
        return enderecoRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public EnderecoCompartilhado create(@RequestBody EnderecoCompartilhado endereco) {
        return enderecoService.compartilhado(endereco);
    }

    @PutMapping("/update/{id}")
    public EnderecoCompartilhado update(@PathVariable UUID id, @RequestBody EnderecoCompartilhado endereco) {
        endereco.setId(id);
        return enderecoRepository.save(endereco);
    }

    @PostMapping("/{id}/retake")
    public EnderecoCompartilhado retake(@PathVariable UUID id) {
        return enderecoService.retake(id);
    }

    @DeleteMapping("/by-address/{addressId}")
    public void releaseByAddress(@PathVariable UUID addressId) {
        enderecoRepository.findAll().stream()
            .filter(x -> addressId.equals(x.getAddressId()))
            .forEach(x -> enderecoService.release(x.getId()));
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        enderecoRepository.deleteById(id);
    }

}
