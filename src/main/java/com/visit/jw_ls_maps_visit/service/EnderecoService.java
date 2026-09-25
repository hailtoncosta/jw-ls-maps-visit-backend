package com.visit.jw_ls_maps_visit.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.visit.jw_ls_maps_visit.model.CardSequence;
import com.visit.jw_ls_maps_visit.model.Endereco;
import com.visit.jw_ls_maps_visit.model.HistoricoVisitas;
import com.visit.jw_ls_maps_visit.repository.CardSequenceRepository;
import com.visit.jw_ls_maps_visit.repository.CongregacaoRepository;
import com.visit.jw_ls_maps_visit.repository.EnderecoRepository;
import com.visit.jw_ls_maps_visit.repository.HistoricoVisitasRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;

import jakarta.transaction.Transactional;

@Service 
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final HistoricoVisitasRepository historicoVisitasRepository;
    private final CardSequenceRepository cardSequenceRepository;
    private final CongregacaoRepository congregacaoRepository;
    private final CurrentUser currentUser;

    public EnderecoService(EnderecoRepository endereco, HistoricoVisitasRepository historico, 
        CardSequenceRepository sequencia, CongregacaoRepository congregacao, CurrentUser usuario) {
            enderecoRepository = endereco;
            historicoVisitasRepository = historico;
            cardSequenceRepository = sequencia;
            congregacaoRepository = congregacao;
            currentUser = usuario;
        }
    
    public List<Endereco> scoped(){
        var usuario = currentUser.get();
        return enderecoRepository.findAll().stream()
            .filter(endereco -> !Boolean.TRUE.equals(endereco.getExcluido()))
            .filter(endereco -> !"superintendente".equalsIgnoreCase(usuario.getRole())
                || usuario.getCircuitoId() != null && usuario.getCircuitoId().equals(endereco.getCircuitoId()))
            .toList();
    }

    @Transactional 
    public Endereco save(Endereco endereco) {

        var usuario = currentUser.get();

        if (endereco.getId() == null) {
            endereco.setCreatedById(usuario.getId());

            if (endereco.getQuantidadeVisitas() == null) endereco.setQuantidadeVisitas(0);
            if (endereco.getExcluido() == null) endereco.setExcluido(false);
            if (endereco.getMerged() == null) endereco.setMerged(false);
        }

        if (endereco.getCongregacao() == null) endereco.setCongregacao(usuario.getCongregation());
        if (endereco.getCircuitoId() == null) endereco.setCircuitoId(usuario.getCircuitoId());
        requireCircuit(endereco);
        return enderecoRepository.save(endereco);
    }

    public Endereco get(UUID id) {
        var endereco = enderecoRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Endereço não encontrado..."));
        requireCircuit(endereco);
        return endereco;
    }

    public void requireCircuit(Endereco endereco) {
        var usuario = currentUser.get();
        if ("superintendente".equalsIgnoreCase(usuario.getRole())
                && (usuario.getCircuitoId() == null || !usuario.getCircuitoId().equals(endereco.getCircuitoId())))
            throw new SecurityException("Endereço fora do circuito do superintendente");
    }

    @Transactional 
    public void softDelete(UUID id) {
        var endereco = get(id);
        checkEdit(endereco);
        endereco.setExcluido(true);
        enderecoRepository.save(endereco);
    }

    @Transactional 
    public void checkEdit(Endereco endereco) {

        var usuario = currentUser.get();
        requireCircuit(endereco);

        if ("admin".equalsIgnoreCase(usuario.getRole())) 
            return;

        if ("superintendente".equalsIgnoreCase(usuario.getRole()) && usuario.getCircuitoId() != null && Objects.equals(usuario.getCircuitoId(), endereco.getCircuitoId()))
            return ;

        if (Objects.equals(usuario.getCongregation(), endereco.getCongregacao()) || Objects.equals(usuario.getId(), endereco.getCreatedById()))
            return ;

        throw new SecurityException("Endereço fora do escopo do usuário...");

    }

    @Transactional 
    public Endereco recordVisit(UUID id, Map<String, Object> p) {

        var endereco = get(id);
        checkEdit(endereco);
        var usuario = currentUser.get();
        var historico = new HistoricoVisitas();

        historico.setAddressId(id);
        historico.setAddressName(endereco.getNome());
        historico.setDataVisita(valueOrDefault(p.get("visit_date"), p.get("data_visita"), java.time.LocalDate.now().toString()));
        historico.setHoraVisita(valueOrDefault(p.get("visit_time"), p.get("hora_visita"), java.time.LocalTime.now().withNano(0).toString()));
        historico.setVisitadoPor(valueOrDefault(p.get("visited_by"), p.get("visitado_por"), usuario.getNomePublicador() != null ? usuario.getNomePublicador() : usuario.getEmail()));
        historico.setCongregacao(endereco.getCongregacao());
        historico.setCircuitoId(endereco.getCircuitoId());
        historico.setNotes((String) p.get("notes"));
        historico.setResultado(valueOrDefault(p.get("result"), p.get("resultado"), null));
        historico.setCreatedById(usuario.getId());
        historicoVisitasRepository.save(historico);

        endereco.setDataUltimaVisita(historico.getDataVisita());
        endereco.setHoraUltimaVisita(historico.getHoraVisita());
        endereco.setVisitadoPor(historico.getVisitadoPor());

        // Permite que o frontend envie alterações do cadastro junto com a visita.
        Object updateObj = ((Map<?, ?>) p).get("address_update");
        if (updateObj instanceof Map<?, ?> updates) {
            applyUpdate(endereco, updates);
        }

        endereco.setQuantidadeVisitas(
            endereco.getQuantidadeVisitas() == null ? 1 : endereco.getQuantidadeVisitas() + 1
        );
        return enderecoRepository.save(endereco);
    }

    private static String valueOrDefault(Object primary, Object legacy, String fallback) {
        Object value = primary != null && !primary.toString().isBlank() ? primary : legacy;
        return value != null && !value.toString().isBlank() ? value.toString() : fallback;
    }

    private void applyUpdate(Endereco e, Map<?, ?> u) {
        if (u.get("status") != null) e.setStatus(String.valueOf(u.get("status")));
        if (u.get("situacao") != null) e.setSituacao(String.valueOf(u.get("situacao")));
        if (u.get("observacao") != null) e.setObservacao(String.valueOf(u.get("observacao")));
        if (u.get("telefone") != null) e.setTelefone(String.valueOf(u.get("telefone")));
        if (u.get("melhor_horario") != null) e.setMelhorHorario(String.valueOf(u.get("melhor_horario")));
        if (u.get("data_ultima_visita") != null) e.setDataUltimaVisita(String.valueOf(u.get("data_ultima_visita")));
        if (u.get("hora_ultima_visita") != null) e.setHoraUltimaVisita(String.valueOf(u.get("hora_ultima_visita")));
        if (u.get("visitado_por") != null) e.setVisitadoPor(String.valueOf(u.get("visitado_por")));
    }

    @Transactional 
    public Endereco assignCard(UUID id) {

        var endereco = get(id);
        checkEdit(endereco);

        if (endereco.getCardNumero() != null)
            return endereco;

        String c = endereco.getCongregacao() == null ? "Sem congregação" : endereco.getCongregacao().trim();

        var s = cardSequenceRepository.findAll().stream().filter(x -> c.equalsIgnoreCase(x.getCongregacao())).findFirst().orElseGet(() -> {
            var x = new CardSequence();
            x.setCongregacao(c);
            x.setLastNumber(0);
            return x;
        });

        int highest = enderecoRepository.findAll().stream()
            .filter(a -> c.equalsIgnoreCase(a.getCongregacao() == null ? "Sem congregação" : a.getCongregacao().trim()))
            .map(Endereco::getCardNumero).filter(Objects::nonNull)
            .mapToInt(Integer::intValue).max().orElse(0);
        s.setLastNumber(Math.max(s.getLastNumber() == null ? 0 : s.getLastNumber(), highest) + 1);
        cardSequenceRepository.save(s);
        endereco.setCardNumero(s.getLastNumber());
        return  enderecoRepository.save(endereco);
    }

    @Transactional
    public int backfillCardNumbers() {
        int assigned = 0;
        var addresses = scoped().stream()
            .filter(a -> !Boolean.TRUE.equals(a.getExcluido()) && a.getCardNumero() == null)
            .sorted(java.util.Comparator.comparing(a -> a.getId().toString()))
            .toList();
        for (var address : addresses) {
            assignCard(address.getId());
            assigned++;
        }
        return assigned;
    }

    @Transactional 
    public void transfer(UUID id, UUID targetCongId) {

        var usuario = currentUser.get();

        if (!usuario.getRole().equalsIgnoreCase("admin") && !usuario.getRole().equalsIgnoreCase("superintendente")) throw new SecurityException("Somente admin ou superintendente...");

        var endereco = get(id);
        var congregacao = congregacaoRepository.findById(targetCongId).orElseThrow();

        if ("superintendente".equalsIgnoreCase(usuario.getRole()) && (usuario.getCircuitoId() == null || !usuario.getCircuitoId().equals(congregacao.getCircuitoId()))) throw new SecurityException("Congregação fora do circuito...");

        endereco.setCongregacao(congregacao.getNome());
        endereco.setCongregacaoId(congregacao.getId());
        endereco.setCircuitoId(congregacao.getCircuitoId());
        enderecoRepository.save(endereco);
    }

    @Transactional 
    public void merge(UUID principalId, UUID duplicateId) {

        var usuario = currentUser.get();

        if (!"admin".equalsIgnoreCase(usuario.getRole()) && !"superintendente".equalsIgnoreCase(usuario.getRole())) throw new SecurityException("Sem permissão");

        var p = get(principalId);
        var d = get(duplicateId);

        p.setQuantidadeVisitas((p.getQuantidadeVisitas() == null? 0 : p.getQuantidadeVisitas()) + (d.getQuantidadeVisitas() == null? 0: d.getQuantidadeVisitas()));

        if (p.getTelefone() == null) p.setTelefone(d.getTelefone());
        if (p.getObservacao() == null) p.setObservacao(d.getObservacao());
        d.setMerged(true);
        d.setEnderecoOrigemId(p.getId());
        d.setDataImportacao(OffsetDateTime.now());
        d.setUsuarioImportacao(usuario.getEmail());
        enderecoRepository.save(d);
        enderecoRepository.save(p);
    }
    
}
