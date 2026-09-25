package com.visit.jw_ls_maps_visit.dto;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InviteUserRequest(
        String email,
        String password,
        String role,
        String congregation,
        @JsonProperty("congregation_id") UUID congregationId,
        @JsonProperty("circuito_id") UUID circuitoId,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("manual_access") String manualAccess,
        Boolean active,
        Set<String> permissions) {
}
