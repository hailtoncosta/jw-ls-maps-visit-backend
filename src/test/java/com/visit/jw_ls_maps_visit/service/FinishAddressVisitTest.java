package com.visit.jw_ls_maps_visit.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import com.visit.jw_ls_maps_visit.model.*;
import com.visit.jw_ls_maps_visit.repository.*;
import com.visit.jw_ls_maps_visit.security.*;
class FinishAddressVisitTest {
 @Test void finishWithoutSharePersistsCompletionAndPreservesHistoryCount() {
  var repo=mock(EnderecoRepository.class); var shares=mock(EnderecoCompartilhadoRepository.class);
  var current=mock(CurrentUser.class); var scope=mock(EnderecoService.class);
  var user=new ContaUsuario();user.setId(UUID.randomUUID());user.setRole("helper");user.setAtivo(true);user.setPermissao(Set.of("action_finish_visit"));
  var address=new Endereco();address.setId(UUID.randomUUID());address.setQuantidadeVisitas(3);
  when(current.get()).thenReturn(user);when(repo.findById(address.getId())).thenReturn(Optional.of(address));
  when(scope.inSuperintendentScope(user,address)).thenReturn(true);when(shares.findAll()).thenReturn(List.of());
  when(repo.save(address)).thenReturn(address);
  var service=new EnderecoCompartilhadoService(shares,mock(ContaUsuarioRepository.class),repo,current,scope,new UserPermissions());
  assertSame(address,service.finishAddress(address.getId()));
  assertNotNull(address.getVisitasFinalizadasEm());assertEquals(user.getId(),address.getVisitasFinalizadasPor());assertEquals(3,address.getQuantidadeVisitas());
  verify(repo).save(address);
  user.setPermissao(Set.of("action_view"));
  assertThrows(AccessDeniedException.class,()->service.finishAddress(address.getId()));
  user.setPermissao(Set.of("action_finish_visit"));when(scope.inSuperintendentScope(user,address)).thenReturn(false);
  assertThrows(SecurityException.class,()->service.finishAddress(address.getId()));
  verify(repo,times(1)).save(address);
 }
}
