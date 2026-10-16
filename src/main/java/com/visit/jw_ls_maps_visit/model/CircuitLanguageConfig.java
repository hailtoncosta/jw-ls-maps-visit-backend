package com.visit.jw_ls_maps_visit.model;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;

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
    @JsonProperty("circuito_name")
    @JsonAlias("nome_circuito")
    private String nomeCircuito;

    @Column (name = "idioma")
    @JsonProperty("language")
    @JsonAlias("idioma")
    private String idioma;

    @Column (name = "speakers")
    private Double speakers;

    @Column (name = "population_ref_value", columnDefinition = "double precision default 0")
    @JsonProperty("population_ref")
    private Double populationRef;

    @Column (name = "configure_by_name")
    @JsonProperty("configured_by_name")
    @JsonAlias("configure_by_name")
    private String configureByName;

}
