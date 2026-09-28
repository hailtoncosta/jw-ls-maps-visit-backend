package com.visit.jw_ls_maps_visit.security;

import java.util.Map;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import com.visit.jw_ls_maps_visit.model.ContaUsuario;

@Component
public class UserPermissions {
    // Empty sets in older accounts mean role defaults. __none__ is an explicit denial.
    private static final Map<String, Set<String>> DEFAULTS = Map.of(
        "publisher", Set.of("page_home", "page_addresses", "page_new_address", "page_map", "page_my_territories", "page_settings", "action_view", "action_create", "action_edit", "action_share", "action_share_internal", "action_import"),
        "helper", Set.of("page_home", "page_addresses", "page_new_address", "page_map", "page_visit_cards", "page_my_territories", "page_progress", "page_settings", "action_view", "action_create", "action_edit", "action_share", "action_share_internal", "action_import"),
        "elder", Set.of("page_home", "page_addresses", "page_new_address", "page_map", "page_visit_cards", "page_my_territories", "page_progress", "page_settings", "action_view", "action_create", "action_edit", "action_share", "action_share_internal", "action_import", "action_export", "admin_manage_users")
    );

    public boolean has(ContaUsuario user, String permission) {
        if (user == null || !Boolean.TRUE.equals(user.getAtivo())) return false;
        String role = user.getRole() == null ? "publisher" : user.getRole().toLowerCase();
        if ("admin".equals(role)) return true;
        Set<String> assigned = user.getPermissao();
        if (assigned != null && !assigned.isEmpty())
            return assigned.contains("*") || assigned.contains(permission);
        if ("superintendente".equals(role)) return true;
        if ("user".equals(role)) role = "publisher";
        return DEFAULTS.getOrDefault(role, Set.of()).contains(permission);
    }

    public void require(ContaUsuario user, String permission) {
        if (!has(user, permission)) throw new AccessDeniedException("Função não liberada: " + permission);
    }
}
