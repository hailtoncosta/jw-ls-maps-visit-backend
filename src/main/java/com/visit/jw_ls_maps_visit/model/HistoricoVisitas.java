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
@Table (name = "historico_visitas")
public class HistoricoVisitas extends BaseEntity{

    @Column (name = "address_id")
    private UUID addressId;

    @Column (name = "address_name")
    private String addressName;

    @Column (name = "data_visita")
    private String dataVisita;

    @Column (name = "hora_visita")
    private String horaVisita;

    @Column (name = "visitado_por")
    private String visitadoPor;

    @Column (name = "congregacao")
    private String congregacao;

    @Column (name = "circuito_id")
    private UUID circuitoId;

    @Column (name = "notes")
    private String notes;

    @Column (name = "resultado")
    private String resultado;
    
}
