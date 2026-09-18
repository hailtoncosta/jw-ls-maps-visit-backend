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
@Table (name = "convite_pendente")
public class ConvitePendente extends BaseEntity {
    
    @Column (name = "nome")
    private String email;
    
    @Column (name = "congregacao")
    private String congregacao;

    @Column (name = "circuito_id")
    private UUID circuitoId;

    @Column (name = "role")
    private String role;

    @Column (name = "manual_access")
    private String manualAccess;

    @Column (name = "applied")
    private String applied;

    @Column (name = "permission_json", columnDefinition = "TEXT")
    private Boolean permissionJson;

    @Column (name = "items_json", columnDefinition = "TEXT")
    private String itemsJson;
    
}
