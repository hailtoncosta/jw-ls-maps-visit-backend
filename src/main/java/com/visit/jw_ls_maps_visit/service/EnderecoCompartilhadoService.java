package com.visit.jw_ls_maps_visit.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.visit.jw_ls_maps_visit.model.EnderecoCompartilhado;
import com.visit.jw_ls_maps_visit.repository.EnderecoCompartilhadoRepository;
import com.visit.jw_ls_maps_visit.repository.ContaUsuarioRepository;
import com.visit.jw_ls_maps_visit.repository.EnderecoRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;
import com.visit.jw_ls_maps_visit.security.UserPermissions;

@Service 
public class EnderecoCompartilhadoService {

    private final EnderecoCompartilhadoRepository enderecoCompartilhadoRepository;
    private final ContaUsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final CurrentUser usuario;
    private final EnderecoService addressScope;
    private final UserPermissions permissions;

    public EnderecoCompartilhadoService (EnderecoCompartilhadoRepository compartilhadoRepository, ContaUsuarioRepository usuarios,
            EnderecoRepository enderecos, CurrentUser user, EnderecoService addressScope, UserPermissions permissions) {

        enderecoCompartilhadoRepository = compartilhadoRepository;
        usuarioRepository = usuarios;
        enderecoRepository = enderecos;
        usuario = user;
        this.addressScope = addressScope;
        this.permissions = permissions;
    }

    public EnderecoCompartilhado compartilhado(EnderecoCompartilhado endereco) {

        var user = usuario.get();
        permissions.require(user, "action_share_internal");
        if (endereco.getAddressId() == null || endereco.getSharedWithUserId() == null)
            throw new IllegalArgumentException("Selecione um endereço e um usuário para compartilhar.");
        if (user.getId().equals(endereco.getSharedWithUserId()))
            throw new IllegalArgumentException("Selecione outro usuário para compartilhar.");
        var address = enderecoRepository.findById(endereco.getAddressId())
            .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado."));
        if (!addressScope.inSuperintendentScope(user, address))
            throw new SecurityException("Compartilhamento fora do circuito ou da congregação do superintendente");
        if (Boolean.TRUE.equals(address.getExcluido()))
            throw new IllegalArgumentException("Endereço excluído não pode ser compartilhado.");
        var target = usuarioRepository.findById(endereco.getSharedWithUserId())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        if (!Boolean.TRUE.equals(target.getAtivo()))
            throw new IllegalArgumentException("Usuário inativo não pode receber compartilhamentos.");
        if ("superintendente".equalsIgnoreCase(user.getRole()) && !user.getCircuitoId().equals(target.getCircuitoId()))
            throw new SecurityException("Destinatário fora do circuito do superintendente");
        if (enderecoCompartilhadoRepository.findAll().stream()
                .anyMatch(item -> endereco.getAddressId().equals(item.getAddressId())))
            throw new IllegalArgumentException("Este endereço já está compartilhado.");

        endereco.setAddressName(address.getNome());
        endereco.setSharedWithName(target.getNomePublicador() != null && !target.getNomePublicador().isBlank()
            ? target.getNomePublicador() : target.getEmail());
        endereco.setCongregacao(address.getCongregacao());
        endereco.setCircuitoId(address.getCircuitoId());
        endereco.setSharedByUserId(user.getId());
        endereco.setSharedByName(user.getNomePublicador() != null ? user.getNomePublicador():user.getEmail());
        return enderecoCompartilhadoRepository.save(endereco);

    }

    public void release(UUID id) {
        permissions.require(usuario.get(), "action_share_internal");
        var end = enderecoCompartilhadoRepository.findById(id).orElseThrow();
        var user = usuario.get();

        if (!user.getRole().equalsIgnoreCase("admin") && !Objects.equals(end.getSharedByUserId(), user.getId()) && !Objects.equals(end.getSharedWithUserId(), user.getId())) throw new SecurityException("Sem permissão...");
        enderecoCompartilhadoRepository.delete(end);
    }

    public EnderecoCompartilhado retake(UUID id) {
        permissions.require(usuario.get(), "action_share_internal");

        var end = enderecoCompartilhadoRepository.findById(id).orElseThrow();
        var user = usuario.get();

        if (!Objects.equals(end.getSharedWithUserId(), user.getId())) throw new SecurityException("Sem permissão...");
        end.setSharedWithUserId(end.getSharedByUserId());
        end.setSharedWithName(end.getSharedByName());
        return enderecoCompartilhadoRepository.save(end);

    }
    
}
