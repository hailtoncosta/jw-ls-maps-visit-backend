package com.visit.jw_ls_maps_visit.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visit.jw_ls_maps_visit.model.CreatorContact;

public interface CreatorContactRepository extends JpaRepository<CreatorContact, UUID> {
    
}
