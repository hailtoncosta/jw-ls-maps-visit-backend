package com.visit.jw_ls_maps_visit.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visit.jw_ls_maps_visit.model.LanguageGoal;

public interface LanguageGoalRepository extends JpaRepository<LanguageGoal, UUID> {
    
}
