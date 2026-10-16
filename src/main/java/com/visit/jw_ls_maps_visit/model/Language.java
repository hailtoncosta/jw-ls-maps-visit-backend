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
@Setter 
@Getter 
@Table(name = "language")
public class Language extends BaseEntity {
    
    @Column (name = "nome", nullable = false)
    @JsonProperty("name")
    @JsonAlias("nome")
    private String nome;

    @Column (name = "nome_alternado")
    @JsonProperty("alternate_name")
    @JsonAlias({"nome_alternado", "nomeAlternado"})
    private String nomeAlternado;

    @Column (name = "codigo")
    @JsonProperty("code")
    @JsonAlias("codigo")
    private String codigo;

    @Column (name = "is_custom")
    @JsonProperty("is_custom")
    private Boolean isCustom;

    @Column (name = "circuito_id")
    @JsonProperty("circuito_id")
    private UUID circuitoId;

    @Column (name = "created_by_nome")
    @JsonProperty("created_by_name")
    @JsonAlias({"created_by_nome", "createdByNome"})
    private String createdByNome;
    
}
