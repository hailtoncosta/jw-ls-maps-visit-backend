package com.visit.jw_ls_maps_visit.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.visit.jw_ls_maps_visit.model.*;
import com.visit.jw_ls_maps_visit.repository.*;
import com.visit.jw_ls_maps_visit.security.*;

class UsuarioSettingsTest {
    private ContaUsuario user;
    private ContaUsuarioRepository users;
    private CongregacaoRepository congregations;
    private UsuarioController controller;

    @BeforeEach void setup() {
        user = new ContaUsuario();
        user.setId(UUID.randomUUID());
        user.setEmail("teste@example.com");
        user.setRole("helper");
        user.setManualAcesso("full");
        user.setCongregation("Atual");
        user.setCongregationId(UUID.randomUUID());
        user.setCircuitoId(UUID.randomUUID());
        user.setPermissao(Set.of("action_edit"));
        users = mock(ContaUsuarioRepository.class);
        congregations = mock(CongregacaoRepository.class);
        CurrentUser current = mock(CurrentUser.class);
        when(current.get()).thenReturn(user);
        when(users.save(any(ContaUsuario.class))).thenAnswer(i -> i.getArgument(0));
        controller = new UsuarioController(users, mock(PasswordEncoder.class), current,
            mock(PasswordResetTokenRepository.class), new UserPermissions(), congregations);
    }

    @Test void savesSettingsWithoutAdministrativePermissionOrChangingAccount() {
        UUID congregationId = user.getCongregationId();
        UUID circuitId = user.getCircuitoId();
        var result = controller.updateMe(Map.of("publisher_name", "Ana", "default_city", "Nampula",
            "default_state", "NA", "congregation", "Atual"));
        assertEquals("Ana", result.getNomePublicador());
        assertEquals("Nampula", result.getCidade());
        assertEquals("NA", result.getEstado());
        assertEquals("helper", result.getRole());
        assertEquals("full", result.getManualAcesso());
        assertEquals("teste@example.com", result.getEmail());
        assertEquals(congregationId, result.getCongregationId());
        assertEquals(circuitId, result.getCircuitoId());
        verifyNoInteractions(congregations);
    }

    @Test void ignoresAdministrativeFieldsInPersonalSettings() {
        controller.updateMe(Map.of("role", "admin", "permissions", List.of("*"),
            "active", false, "email", "outro@example.com", "manual_access", "basic"));
        assertEquals("helper", user.getRole());
        assertEquals(Set.of("action_edit"), user.getPermissao());
        assertTrue(user.getAtivo());
        assertEquals("teste@example.com", user.getEmail());
        assertEquals("full", user.getManualAcesso());
    }

    @Test void changingCongregationKeepsItsIdAndCircuitConsistent() {
        Congregacao next = new Congregacao();
        next.setId(UUID.randomUUID()); next.setNome("Nova"); next.setCircuitoId(UUID.randomUUID());
        when(congregations.findAll()).thenReturn(List.of(next));
        controller.updateMe(Map.of("congregation", "Nova"));
        assertEquals(next.getId(), user.getCongregationId());
        assertEquals(next.getCircuitoId(), user.getCircuitoId());
    }

    @Test void superintendentCannotMoveItsOwnScopeThroughSettings() {
        user.setRole("superintendente");
        Congregacao next = new Congregacao(); next.setNome("Nova");
        when(congregations.findAll()).thenReturn(List.of(next));
        assertThrows(SecurityException.class, () -> controller.updateMe(Map.of("congregation", "Nova")));
        verify(users, never()).save(any());
        assertEquals("Atual", user.getCongregation());
    }
}
