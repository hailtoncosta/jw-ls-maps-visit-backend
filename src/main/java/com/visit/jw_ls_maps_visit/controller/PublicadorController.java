package com.visit.jw_ls_maps_visit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visit.jw_ls_maps_visit.model.Endereco;
import com.visit.jw_ls_maps_visit.repository.EnderecoRepository;

@RestController 
@RequestMapping("/api/public")
public class PublicadorController {

    private final EnderecoRepository enderecoRepository;

    public PublicadorController(EnderecoRepository endereco) {
        enderecoRepository = endereco;
    }

    @GetMapping("/enderecos/{id}")
    public Endereco enderecos(@PathVariable  UUID id) {
        var endereco = enderecoRepository.findById(id).orElseThrow();
        if (Boolean.TRUE.equals(endereco.getExcluido())) throw new NoSuchElementException("Cadastro não encontrado...");
        return endereco;
    }

    @GetMapping("/digital-card/{ids}")
    public Map<String, Object> digitalCard(@PathVariable String ids) {
        List<Endereco> out = new ArrayList<>();

        for (String x: ids.split(",")) {
            try {
                var endereco = enderecoRepository.findById(UUID.fromString(x.trim())).orElse(null);
                if (endereco != null && !Boolean.TRUE.equals(endereco.getExcluido())) out.add(endereco);
            } catch (Exception e) {}
        }
        return Map.of("enderecos", out);
    }
    
}
