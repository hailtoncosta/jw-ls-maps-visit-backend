package com.visit.jw_ls_maps_visit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@Table (name = "population_override")
public class PopulationOverride extends BaseEntity {
    
    @Column (name = "city_key")
    private String cityKey;

    @Column (name = "city_name")
    private String cityName;

    @Column (name = "population")
    private Double population;
    
}
