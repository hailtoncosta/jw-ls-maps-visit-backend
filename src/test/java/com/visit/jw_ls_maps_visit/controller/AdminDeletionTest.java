package com.visit.jw_ls_maps_visit.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.visit.jw_ls_maps_visit.model.*;
import com.visit.jw_ls_maps_visit.repository.*;
import com.visit.jw_ls_maps_visit.security.*;

class AdminDeletionTest {
    @Test void circuitWithCongregationsIsBlockedIncludingLegacyNameLinks() {
        var circuits = mock(CircuitoRepository.class);
        var congregations = mock(CongregacaoRepository.class);
        var current = mock(CurrentUser.class);
        var user = new ContaUsuario(); user.setAtivo(true); user.setRole("admin");
        when(current.get()).thenReturn(user);
        var circuit = new Circuito(); circuit.setId(UUID.randomUUID()); circuit.setNome("C1");
        when(circuits.findById(circuit.getId())).thenReturn(Optional.of(circuit));
        var congregation = new Congregacao(); congregation.setCircuitoId(circuit.getId());
        when(congregations.findAll()).thenReturn(List.of(congregation));
        var controller = new CircuitoController(circuits, current, congregations);
        assertThrows(IllegalArgumentException.class, () -> controller.delete(circuit.getId()));
        congregation.setCircuitoId(null); congregation.setCircuito(" c1 ");
        assertThrows(IllegalArgumentException.class, () -> controller.delete(circuit.getId()));
        verify(circuits, never()).deleteById(any());
        when(congregations.findAll()).thenReturn(List.of());
        controller.delete(circuit.getId());
        verify(circuits).deleteById(circuit.getId());
        user.setRole("superintendente");
        assertThrows(AccessDeniedException.class, () -> controller.delete(circuit.getId()));
        verify(circuits, times(1)).deleteById(any());
    }

    @Test void selfDeletionIsBlockedButAnotherAccountCanBeDeleted() {
        var users = mock(ContaUsuarioRepository.class);
        var current = mock(CurrentUser.class);
        var tokens = mock(PasswordResetTokenRepository.class);
        var user = new ContaUsuario(); user.setId(UUID.randomUUID()); user.setAtivo(true); user.setRole("admin");
        when(current.get()).thenReturn(user);
        when(current.isAny("admin", "superintendente")).thenReturn(true);
        var controller = new UsuarioController(users, mock(PasswordEncoder.class), current, tokens,
            new UserPermissions(), mock(CongregacaoRepository.class));
        assertThrows(AccessDeniedException.class, () -> controller.delete(user.getId()));
        verify(users, never()).deleteById(any());
        var other = new ContaUsuario(); other.setId(UUID.randomUUID());
        when(users.findById(other.getId())).thenReturn(Optional.of(other));
        when(tokens.findByUserId(other.getId())).thenReturn(Optional.empty());
        controller.delete(other.getId());
        verify(users).deleteById(other.getId());
    }
}
