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
@Table(name = "language")
public class Language extends BaseEntity {
    
    @Column (name = "nome", nullable = false)
    private String nome;

    @Column (name = "nome_alternado")
    private String nomeAlternado;

    @Column (name = "codigo")
    private String codigo;

    @Column (name = "is_custom")
    private Boolean isCustom;

    @Column (name = "circuito_id")
    private UUID circuitoId;

    @Column (name = "created_by_nome")
    private String CreatedByNome;
    
}
