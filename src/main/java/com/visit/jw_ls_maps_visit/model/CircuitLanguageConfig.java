package com.visit.jw_ls_maps_visit.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter
@Table (name = "circuit_language_config")
public class CircuitLanguageConfig extends BaseEntity {
    
    @Column (name = "circuito_id")
    private UUID circuitoId;

    @Column (name = "nome_circuito")
    private String nomeCircuito;

    @Column (name = "idioma")
    private String idioma;

    @Column (name = "speakers")
    private Double speakers;

    @Column (name = "population_ref")
    private UUID populationRef;

    @Column (name = "configure_by_name")
    private String configureByName;

}
