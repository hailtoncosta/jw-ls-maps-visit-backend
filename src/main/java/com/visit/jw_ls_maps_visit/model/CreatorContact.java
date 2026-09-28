package com.visit.jw_ls_maps_visit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@Table (name = "creator_contact")
public class CreatorContact extends BaseEntity {
    
    @Column (name = "contact_email")
    private String contactEmail;

    @Column (name = "whatsapp_number")
    private String whatsappNumber;

    @Column (name = "whatsapp_message", columnDefinition = "TEXT")
    private String whatsappMessage;
    
}
