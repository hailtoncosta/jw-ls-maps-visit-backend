package com.visit.jw_ls_maps_visit.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.visit.jw_ls_maps_visit.model.EnderecoCompartilhado;
import com.visit.jw_ls_maps_visit.repository.EnderecoCompartilhadoRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;

@Service 
public class EnderecoCompartilhadoService {

    private final EnderecoCompartilhadoRepository enderecoCompartilhadoRepository;
    private final CurrentUser usuario;

    public EnderecoCompartilhadoService (EnderecoCompartilhadoRepository compartilhadoRepository, CurrentUser user) {

        enderecoCompartilhadoRepository = compartilhadoRepository;
        usuario = user;
    }

    public EnderecoCompartilhado compartilhado(EnderecoCompartilhado endereco) {

        var user = usuario.get();

        endereco.setSharedByUserId(user.getId());
        endereco.setSharedByName(user.getNomePublicador() != null ? user.getNomePublicador():user.getEmail());
        return enderecoCompartilhadoRepository.save(endereco);

    }

    public void release(UUID id) {
        var end = enderecoCompartilhadoRepository.findById(id).orElseThrow();
        var user = usuario.get();

        if (!user.getRole().equalsIgnoreCase("admin") && !Objects.equals(end.getSharedByUserId(), user.getId()) && !Objects.equals(end.getSharedWithUserId(), user.getId())) throw new SecurityException("Sem permissão...");
        enderecoCompartilhadoRepository.delete(end);
    }

    public EnderecoCompartilhado retake(UUID id) {

        var end = enderecoCompartilhadoRepository.findById(id).orElseThrow();
        var user = usuario.get();

        if (!Objects.equals(end.getSharedWithUserId(), user.getId())) throw new SecurityException("Sem permissão...");
        end.setSharedWithUserId(end.getSharedByUserId());
        end.setSharedWithName(end.getSharedByName());
        return enderecoCompartilhadoRepository.save(end);

    }
    
}
