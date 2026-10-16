package com.visit.jw_ls_maps_visit.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.visit.jw_ls_maps_visit.model.*;
import com.visit.jw_ls_maps_visit.repository.*;
import com.visit.jw_ls_maps_visit.security.*;
class RetakeSharedAddressTest {
 EnderecoCompartilhadoRepository shares; EnderecoRepository addresses; EnderecoService scope;
 ContaUsuario user; EnderecoCompartilhado share; Endereco address; EnderecoCompartilhadoService service;
 @BeforeEach void setup() {
  shares=mock(EnderecoCompartilhadoRepository.class); addresses=mock(EnderecoRepository.class); scope=mock(EnderecoService.class);
  user=new ContaUsuario(); user.setId(UUID.randomUUID()); user.setRole("helper");
  share=new EnderecoCompartilhado(); share.setId(UUID.randomUUID()); share.setAddressId(UUID.randomUUID());
  share.setSharedByUserId(user.getId()); share.setSharedWithUserId(UUID.randomUUID());
  address=new Endereco(); address.setId(share.getAddressId());
  CurrentUser current=mock(CurrentUser.class); when(current.get()).thenReturn(user);
  when(shares.findById(share.getId())).thenReturn(Optional.of(share));
  when(addresses.findById(address.getId())).thenReturn(Optional.of(address));
  when(scope.inSuperintendentScope(user,address)).thenReturn(true);
  service=new EnderecoCompartilhadoService(shares,mock(ContaUsuarioRepository.class),addresses,current,scope,mock(UserPermissions.class));
 }
 @Test void senderRetakesWithoutReassigningOrDeletingAddress() {
  UUID recipient=share.getSharedWithUserId(); assertSame(share,service.retake(share.getId()));
  verify(shares).delete(share); verify(shares,never()).save(any()); verify(addresses,never()).delete(any());
  assertEquals(recipient,share.getSharedWithUserId());
 }
 @Test void recipientCanReturnSharedAddress() { user.setId(share.getSharedWithUserId()); service.retake(share.getId()); verify(shares).delete(share); }
 @Test void unrelatedUserCannotRetake() {
  user.setId(UUID.randomUUID()); assertThrows(SecurityException.class,()->service.retake(share.getId())); verify(shares,never()).delete(any());
 }
 @Test void superintendentCannotRetakeOutsideScope() {
  user.setRole("superintendente"); when(scope.inSuperintendentScope(user,address)).thenReturn(false);
  assertThrows(SecurityException.class,()->service.retake(share.getId())); verify(shares,never()).delete(any());
 }
 @Test void publisherCanFinishReceivedShare() {
  user.setRole("publisher"); user.setId(share.getSharedWithUserId()); service.finish(share.getId()); verify(shares).delete(share);
 }
 @Test void helperCannotFinishAnotherRecipientsShare() {
  user.setRole("helper"); user.setId(UUID.randomUUID()); assertThrows(SecurityException.class,()->service.finish(share.getId())); verify(shares,never()).delete(any());
 }
}
