package com.visit.jw_ls_maps_visit.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@Table (name = "circuitos")
public class Circuito extends BaseEntity {
    
    @JsonProperty("name")
    @JsonAlias("nome")
    @Column (name = "nome", nullable = false)
    private String nome;
    
}
