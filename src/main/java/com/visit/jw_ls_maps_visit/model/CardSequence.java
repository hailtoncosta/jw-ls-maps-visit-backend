package com.visit.jw_ls_maps_visit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter
@Table (name = "card_sequence")
public class CardSequence extends BaseEntity {
    
    @Column (name = "congregacao")
    private String congregacao;

    @Column (name = "last_number")
    private Integer lastNumber;
    
}
