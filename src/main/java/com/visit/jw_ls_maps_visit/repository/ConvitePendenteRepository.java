package com.visit.jw_ls_maps_visit.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visit.jw_ls_maps_visit.model.ConvitePendente;

public interface ConvitePendenteRepository extends JpaRepository<ConvitePendente, UUID> {
    
}
