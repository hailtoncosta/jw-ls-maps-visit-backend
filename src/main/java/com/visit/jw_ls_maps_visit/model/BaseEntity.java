package com.visit.jw_ls_maps_visit.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@MappedSuperclass 
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "created_by_id")
    private UUID createdById;

    @PrePersist
    protected void prePersist() {

        OffsetDateTime now = OffsetDateTime.now();

        if (this.createdAt == null) {
            this.createdAt = now;
        }

        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    protected void preUpdate() {

        this.updatedAt = OffsetDateTime.now();

        // Proteção adicional para dados antigos/incompletos
        if (this.createdAt == null) {
            this.createdAt = this.updatedAt;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @JsonProperty("created_date")
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_date")
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @JsonProperty("updated_date")
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_date")
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("created_by_id")
    public UUID getCreatedById() {
        return createdById;
    }

    @JsonProperty("created_by_id")
    public void setCreatedById(UUID createdById) {
        this.createdById = createdById;
    }
}
