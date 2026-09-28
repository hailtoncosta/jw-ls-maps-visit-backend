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

    public EnderecoController (EnderecoService endService, EnderecoRepository endRepository,
            HistoricoVisitasRepository historico) {
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
    public Endereco update(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        var endereco = enderecoService.get(id);
        enderecoService.checkEdit(endereco);
        // Atualiza a entidade carregada do banco, sem aceitar ID, autoria ou
        // número do cartão vindos do navegador. Null limpa campos opcionais.
        updates.forEach((key, value) -> {
            switch (key) {
                case "congregation" -> endereco.setCongregacao((String) value);
                case "congregation_id" -> endereco.setCongregacaoId(uuid(value));
                case "circuito_id" -> { if (value != null && !value.toString().isBlank()) endereco.setCircuitoId(uuid(value)); }
                case "name" -> endereco.setNome((String) value);
                case "gender" -> endereco.setGenero((String) value);
                case "age" -> endereco.setIdade(integer(value));
                case "street" -> endereco.setRua((String) value);
                case "neighborhood" -> endereco.setBairro((String) value);
                case "city" -> endereco.setCidade((String) value);
                case "state" -> endereco.setEstado((String) value);
                case "lat" -> endereco.setLatitude(decimal(value));
                case "lng" -> endereco.setLongitude(decimal(value));
                case "maps_link" -> endereco.setMapsLink((String) value);
                case "phone" -> endereco.setTelefone((String) value);
                case "observations" -> endereco.setObservacao((String) value);
                case "last_visit_date" -> endereco.setDataUltimaVisita((String) value);
                case "last_visit_time" -> endereco.setHoraUltimaVisita((String) value);
                case "last_visited_by" -> endereco.setVisitadoPor((String) value);
                case "visit_count" -> endereco.setQuantidadeVisitas(integer(value));
                case "status" -> endereco.setStatus((String) value);
                case "situacao" -> endereco.setSituacao((String) value);
                case "reference_point" -> endereco.setPontoReferencia((String) value);
                case "house_photo" -> endereco.setFotoCasa((String) value);
                case "best_time" -> endereco.setMelhorHorario((String) value);
                case "language" -> endereco.setIdioma((String) value);
                case "language_level" -> endereco.setNivelIdioma((String) value);
                case "sign_language_level" -> endereco.setNivelIdiomaSinal((String) value);
                case "deleted" -> endereco.setExcluido((Boolean) value);
                default -> { /* Ignora campos de identidade e desconhecidos. */ }
            }
        });
        return enderecoService.save(endereco);
    }

    private static UUID uuid(Object value) {
        return value == null || value.toString().isBlank() ? null : UUID.fromString(value.toString());
    }

    private static Integer integer(Object value) {
        return value == null || value.toString().isBlank() ? null : Integer.valueOf(value.toString());
    }

    private static Double decimal(Object value) {
        return value == null || value.toString().isBlank() ? null : Double.valueOf(value.toString());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable  UUID id) {
        enderecoService.softDelete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/{id}/visitas")
    public List<?> historico(@PathVariable UUID id){
        enderecoService.get(id);
        return historicoVisitasRepository.findAll().stream().filter(v -> id.equals(v.getAddressId())).toList();
    }

    @GetMapping("/{id}/digital-card")
    public Endereco digitalCard(@PathVariable UUID id) {
        return enderecoService.get(id);
    }



}
