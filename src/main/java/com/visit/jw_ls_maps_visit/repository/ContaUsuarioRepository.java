package com.visit.jw_ls_maps_visit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visit.jw_ls_maps_visit.model.ContaUsuario;

public interface ContaUsuarioRepository extends JpaRepository<ContaUsuario, UUID> {

    Optional<ContaUsuario> findByEmailIgnoreCase(String email);

    List<ContaUsuario> findByCongregationId(UUID congregationId);

    List<ContaUsuario> findByCongregation(String congregation);

    List<ContaUsuario> findByCircuitoId(UUID circuitoId);
    
}
