package com.visit.jw_ls_maps_visit.controller;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visit.jw_ls_maps_visit.service.EnderecoService;

@RestController 
@RequestMapping("/api/visita")
public class VisitaController {

    private final EnderecoService enderecoService;

    public VisitaController(EnderecoService endereco) {
        enderecoService = endereco;
    }
    
    @SuppressWarnings("")
    @PostMapping("/record")
    public Object record(@RequestBody Map<String, Object> b) {
        UUID id = UUID.fromString(String.valueOf(b.get("address_id")));
        Map<String, Object> payload = new HashMap<>();
        Object visit = b.get("visit_payload");
        if (visit instanceof Map<?, ?> m) {
            m.forEach((k, v) -> payload.put(String.valueOf(k), v));
        } else {
            b.forEach((k, v) -> payload.put(String.valueOf(k), v));
        }
        payload.put("address_update", b.get("address_update"));
        return enderecoService.recordVisit(id, payload);
    }
}
