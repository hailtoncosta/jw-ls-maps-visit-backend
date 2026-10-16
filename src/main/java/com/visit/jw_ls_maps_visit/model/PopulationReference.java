package com.visit.jw_ls_maps_visit.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity @Getter @Setter @Table(name="population_reference")
public class PopulationReference {
 @Id private String id;
 private String city;
 private Long population;
 private Integer year;
 private String scope;
 @Column(length=2000) private String source;
 private String checkedAt;
}
