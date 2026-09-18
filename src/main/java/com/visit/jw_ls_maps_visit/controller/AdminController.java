package com.visit.jw_ls_maps_visit.controller;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visit.jw_ls_maps_visit.model.Endereco;
import com.visit.jw_ls_maps_visit.repository.CircuitoRepository;
import com.visit.jw_ls_maps_visit.repository.CongregacaoRepository;
import com.visit.jw_ls_maps_visit.repository.ContaUsuarioRepository;
import com.visit.jw_ls_maps_visit.repository.EnderecoRepository;
import com.visit.jw_ls_maps_visit.repository.HistoricoVisitasRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;
import com.visit.jw_ls_maps_visit.service.EnderecoService;

@RestController 
@RequestMapping("/api/admin")
public class AdminController {
    
    private final CurrentUser curretUser;
    private final EnderecoService endService;
    private final EnderecoRepository endRepository;
    private final HistoricoVisitasRepository histRepository;
    private final CongregacaoRepository congRepository;
    private final CircuitoRepository circRepository;
    private final ContaUsuarioRepository contaRepository;

    public AdminController(CurrentUser user, EnderecoService end, EnderecoRepository endRepo, HistoricoVisitasRepository histVisRepo, 
        CongregacaoRepository congRepo, CircuitoRepository circRepo, ContaUsuarioRepository contaRepo) {
        curretUser = user;
        endService = end;
        endRepository = endRepo;
        histRepository = histVisRepo;
        congRepository = congRepo;
        circRepository = circRepo;
        contaRepository = contaRepo;
       
    }

    @SuppressWarnings("")
    private void admin() {
        if (!curretUser.is("admin")) throw new SecurityException("Somente administrador...");
    }

    @PostMapping("/enderecos/{id}/assign-card")
    public Endereco card(@PathVariable UUID id) {
        return endService.assignCard(id);
    }

    @PostMapping("/backfill-card-numbers")
    public Map<String, Object> backFill() {
        admin();
        int n = 0;
            for(var end:endRepository.findAll()) {
                if (end.getCardNumero() == null && !Boolean.TRUE.equals(end.getExcluido())) {
                    n++;
                }
            }
        return Map.of("assigned", n);
    }

    @PostMapping("/enderecos/transfer")
    public Map<String, Object> transfer(@RequestBody Map<String, String> b) {
        endService.transfer(UUID.fromString(b.get("principalId")), UUID.fromString(b.get("duplicateId")));
        return Map.of("success", true);
    }

    @PostMapping("/enderecos/merge")
    public Map<String, Object> merge(@RequestBody Map<String, String> b) {
        endService.merge(UUID.fromString(b.get("principalId")), UUID.fromString(b.get("duplicateId")));
        return Map.of("success", true);
    }

    @PostMapping("/congregations/transfer-circuit")
    public Map<String, Object> transfCong(@RequestBody Map<String, String> b) {
        admin();

        var cong = congRepository.findById(UUID.fromString(b.get("congregationId"))).orElseThrow();
        var ncir = circRepository.findById(UUID.fromString(b.get("newCircuitoId"))).orElseThrow();
        cong.setCircuitoId(ncir.getId());
        cong.setCircuito(ncir.getNome());
        congRepository.save(cong);

        for (var a: endRepository.findAll())
            if (Objects.equals(a.getCongregacaoId(), cong.getId()) || Objects.equals(a.getCongregacao(), cong.getNome())) {
                a.setCircuitoId(cong.getCircuitoId());
                endRepository.save(a);
            }
        return Map.of("success", true);
    }

    @PostMapping("/transfer")
    public Map<String, Object> transferAdmin(@RequestBody Map<String, String> b) {
        admin();

        var old = curretUser.get();
        var target = contaRepository.findById(UUID.fromString(b.get("targetUserId"))).orElseThrow();
        old.setRole("elder");
        target.setRole("admin");
        contaRepository.save(old);
        contaRepository.save(target);
        return Map.of("success", true);

    }

    @PostMapping("/undo-transfer")
    public Map<String, Object> undoTransfer() {
        return Map.of("success", false, "message", "A reversão da transferência administrativa não está disponível nesta versão local.");
    }

    public Map<String, Object> backup() {
        admin();

        return Map.of("app", "LSMapasVisit","exported_at", java.time.OffsetDateTime.now(), "enderecos", endRepository.findAll(), 
        "visit_histories", histRepository.findAll());
    }

}
