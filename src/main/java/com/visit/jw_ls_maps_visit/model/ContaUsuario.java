package com.visit.jw_ls_maps_visit.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter 
@Setter 
@Table (name = "usuarios", indexes = {
    @Index(name = "idx_usuario_email", columnList = "email", unique = true)
})
public class ContaUsuario extends BaseEntity {
    
    @Column (nullable = false, unique = true)
    private String email;

    @Column (name = "password_hash")
    private String passwordHash;

    private String role = "publisher";

    private String congregation;

    @Column (name = "congregation_id")
    private UUID congregationId;

    @Column (name = "circuito_id")
    private UUID circuitoId;

    @JsonProperty("full_name")
    @JsonAlias("nome_publicador")
    private String nomePublicador;

    private String cidade;

    private String estado;

    @JsonProperty("manual_access")
    @JsonAlias("manual_acesso")
    private String manualAcesso = "basic";

    @JsonProperty("active")
    @JsonAlias("ativo")
    private Boolean ativo = true;

    @Column (name = "pending_reversal_json")
    private String pendingReversalJson;

    @ElementCollection 
    @CollectionTable (name = "usuario_permissoes", joinColumns = @jakarta.persistence.JoinColumn(name = "usuario_id"))
    @Column (name = "permissao")
    @JsonProperty("permissions")
    @JsonAlias("permissao")
    private Set<String> permissao = new HashSet<>();

}
