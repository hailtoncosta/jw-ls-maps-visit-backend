package com.visit.jw_ls_maps_visit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import com.visit.jw_ls_maps_visit.model.*;
import com.visit.jw_ls_maps_visit.repository.*;
import com.visit.jw_ls_maps_visit.security.*;

class EnderecoPermissionsTest {
    private EnderecoRepository repository;
    private EnderecoService service;
    private ContaUsuario user;
    private Endereco address;

    @BeforeEach void setup() {
        repository = mock(EnderecoRepository.class);
        CurrentUser current = mock(CurrentUser.class);
        user = new ContaUsuario();
        user.setId(UUID.randomUUID());
        user.setAtivo(true);
        user.setRole("helper");
        user.setCircuitoId(UUID.randomUUID());
        user.setCongregation("Congregação do usuário");
        address = new Endereco();
        address.setId(UUID.randomUUID());
        address.setCreatedById(UUID.randomUUID());
        address.setCircuitoId(UUID.randomUUID());
        address.setCongregacao("Outra congregação");
        when(current.get()).thenReturn(user);
        when(repository.findById(address.getId())).thenReturn(Optional.of(address));
        when(repository.save(any(Endereco.class))).thenAnswer(i -> i.getArgument(0));
        service = new EnderecoService(repository, mock(HistoricoVisitasRepository.class),
            mock(CardSequenceRepository.class), mock(CongregacaoRepository.class), current, new UserPermissions());
    }

    @Test void congregationListScopeAppliesToAllThreeRolesAndLegacyUser() {
        for (String role : java.util.List.of("elder", "helper", "publisher", "user")) {
            user.setRole(role);
            user.setPermissao(Set.of("*"));
            Endereco own = new Endereco();
            own.setCongregacao("  congregação do usuário ");
            when(repository.findAll()).thenReturn(java.util.List.of(address, own));
            assertEquals(java.util.List.of(own), service.scoped());
            own.setCongregacaoId(UUID.randomUUID());
            user.setCongregationId(UUID.randomUUID());
            assertTrue(service.scoped().isEmpty(), "Different IDs must override matching names");
            own.setCongregacaoId(user.getCongregationId());
            own.setCongregacao("Nome antigo");
            user.setCongregation(null);
            assertEquals(java.util.List.of(own), service.scoped());
            user.setCongregationId(null);
            assertTrue(service.scoped().isEmpty(), "Missing congregation must not grant global access");
            user.setCongregation("Congregação do usuário");
        }
    }

    @Test void administratorKeepsGlobalListVisibility() {
        user.setRole("admin");
        when(repository.findAll()).thenReturn(java.util.List.of(address));
        assertEquals(java.util.List.of(address), service.scoped());
    }

    @Test void superintendentListsAllCongregationsInOwnCircuitOnly() {
        user.setRole("superintendente");
        Endereco anotherCongregation = new Endereco();
        anotherCongregation.setCircuitoId(user.getCircuitoId());
        anotherCongregation.setCongregacao("Outra congregação do circuito");
        Endereco missingCircuit = new Endereco();
        when(repository.findAll()).thenReturn(java.util.List.of(address, anotherCongregation, missingCircuit));
        assertEquals(java.util.List.of(anotherCongregation), service.scoped());
        user.setCongregation(null);
        assertEquals(java.util.List.of(anotherCongregation), service.scoped());
        user.setCircuitoId(null);
        assertTrue(service.scoped().isEmpty());
    }

    @Test void onlyActiveGeneralAdministratorCanClearAddresses() {
        for (String role : java.util.List.of("publisher", "helper", "elder", "superintendente")) {
            user.setRole(role);
            user.setPermissao(Set.of("*"));
            assertThrows(AccessDeniedException.class,
                () -> service.clearAllAddresses("EXCLUIR TODOS OS ENDEREÇOS"));
        }
        user.setRole("admin");
        user.setAtivo(false);
        assertThrows(AccessDeniedException.class,
            () -> service.clearAllAddresses("EXCLUIR TODOS OS ENDEREÇOS"));
        verify(repository, never()).deleteAll();
    }

    @Test void clearRequiresExactConfirmationAndDeletesAllRecords() {
        user.setRole("admin");
        assertThrows(IllegalArgumentException.class, () -> service.clearAllAddresses(null));
        assertThrows(IllegalArgumentException.class, () -> service.clearAllAddresses("confirmar"));
        verify(repository, never()).deleteAll();
        when(repository.count()).thenReturn(15L);
        assertEquals(15L, service.clearAllAddresses("EXCLUIR TODOS OS ENDEREÇOS"));
        verify(repository).deleteAll();
        verify(repository).flush();
    }

    @Test void editorCanSaveAnotherAuthorsAddressAndReadResult() {
        user.setPermissao(Set.of("action_edit"));
        assertSame(address, service.getForEdit(address.getId()));
        address.setNome("Nome alterado");
        assertEquals("Nome alterado", service.save(address).getNome());
        assertSame(address, service.get(address.getId()));
        verify(repository).save(address);
    }

    @Test void deleteDoesNotRequireEditOrView() {
        user.setPermissao(Set.of("action_delete"));
        service.softDelete(address.getId());
        assertTrue(address.getExcluido());
        verify(repository).save(address);
        assertThrows(AccessDeniedException.class, () -> service.getForEdit(address.getId()));
    }

    @Test void viewerCannotSaveOrDelete() {
        user.setPermissao(Set.of("action_view"));
        assertThrows(AccessDeniedException.class, () -> service.save(address));
        assertThrows(AccessDeniedException.class, () -> service.softDelete(address.getId()));
        verify(repository, never()).save(any());
    }

    @Test void superintendentCannotEditOrDeleteOutsideScope() {
        user.setRole("superintendente");
        user.setPermissao(Set.of("action_edit", "action_delete"));
        assertThrows(SecurityException.class, () -> service.getForEdit(address.getId()));
        assertThrows(SecurityException.class, () -> service.save(address));
        assertThrows(SecurityException.class, () -> service.softDelete(address.getId()));
        verify(repository, never()).save(any());
    }

    @Test void superintendentCanEditInsideScope() {
        user.setRole("superintendente");
        user.setPermissao(Set.of("action_edit"));
        address.setCircuitoId(user.getCircuitoId());
        address.setCongregacao(user.getCongregation());
        assertSame(address, service.save(address));
    }

    @Test void inactiveUserCannotEditOrDelete() {
        user.setAtivo(false);
        user.setPermissao(Set.of("action_edit", "action_delete"));
        assertThrows(AccessDeniedException.class, () -> service.save(address));
        assertThrows(AccessDeniedException.class, () -> service.softDelete(address.getId()));
        verify(repository, never()).save(any());
    }
}
