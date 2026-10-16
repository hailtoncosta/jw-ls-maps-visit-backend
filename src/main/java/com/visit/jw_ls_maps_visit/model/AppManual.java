package com.visit.jw_ls_maps_visit.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "app_manual")
public class AppManual extends BaseEntity {

    @Column(name = "manual_type")
    private String manualType;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "generated_at")
    private OffsetDateTime generatedAt;

    @Column(name = "generated_by")
    private UUID generatedBy;
}
