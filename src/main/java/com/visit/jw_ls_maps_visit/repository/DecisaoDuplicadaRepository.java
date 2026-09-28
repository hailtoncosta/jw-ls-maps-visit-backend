package com.visit.jw_ls_maps_visit.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visit.jw_ls_maps_visit.model.DecisaoDuplicada;

public interface DecisaoDuplicadaRepository extends JpaRepository<DecisaoDuplicada, UUID>{
    
}
