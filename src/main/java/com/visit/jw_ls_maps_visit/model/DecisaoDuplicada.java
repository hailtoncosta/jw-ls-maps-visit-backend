package com.visit.jw_ls_maps_visit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Setter 
@Getter 
@Table (name = "decisao_duplicada")
public class DecisaoDuplicada extends BaseEntity {
    
    @Column (name = "address_a_id", nullable = false)
    private Integer addressAId;

    @Column (name = "address_b_id")
    private Integer addressBId;

    @Column (name = "chave_par")
    private String chavePar;

    @Column (name = "decisao")
    private String decisao;

    @Column (name = "merged_principal_id")
    private Integer mergedPrincipalId;

    @Column (name = "merged_duplicated_id")
    private Integer mergedDuplicatedId;

    @Column (name = "decide_by")
    private String decideBy;

    @Column (name = "reason")
    private String reason;

    @Column (name = "fields_summary")
    private String fieldsSummary;

    @Column (name = "congregacao")
    private String congregacao;

    @Column (name = "circuito_id")
    private Integer circuitoId;
}