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
@Table (name = "endereco_compartilhado")
public class EnderecoCompartilhado extends BaseEntity {

    @Column (name = "addess_id")
    private UUID addressId;

    @Column (name = "address_name")
    private String addressName;

    @Column (name = "shared_by_user_id")
    private UUID sharedByUserId;

    @Column (name = "shared_by_name")
    private String sharedByName;

    @Column (name = "shared_with_user_id")
    private UUID sharedWithUserId;

    @Column (name = "shared_with_name")
    private String SharedWithName;

    @Column (name = "congregacao")
    private String congregacao;

    @Column (name = "circuito_id")
    private UUID circuitoId;
    
}
