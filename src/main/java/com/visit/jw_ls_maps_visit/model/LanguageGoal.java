package com.visit.jw_ls_maps_visit.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "language_goal")
public class LanguageGoal extends BaseEntity {
    
    @Column (name = "language_id")
    private UUID circuitoId;

    @Column (name = "language_nome")
    private String circuitoNome;

    @Column (name = "area")
    private String area;

    @Column (name = "speakers")
    private Double speakers;

    @Column (name = "population_ref")
    private Double populationRef;

    @Column (name = "configure_by")
    private UUID configureBy;

    @Column (name = "configure_by_nome")
    private String configureByNome;
    
}
