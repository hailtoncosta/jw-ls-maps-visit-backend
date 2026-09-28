package com.visit.jw_ls_maps_visit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Setter 
@Getter 
@Table (name = "contact_message")
public class ContactMessage extends BaseEntity {
    
    @Column (name = "nome")
    private String nome;

    @Column (name = "email")
    private String email;

    @Column (name = "assunto")
    private String assunto;

    @Column (name = "mensagem", columnDefinition = "TEXT")
    private String mensagem;
    
}
