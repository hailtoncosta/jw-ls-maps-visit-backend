package com.visit.jw_ls_maps_visit.service;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;
import org.junit.jupiter.api.Test;
import com.visit.jw_ls_maps_visit.model.ContaUsuario;
import com.visit.jw_ls_maps_visit.security.UserPermissions;
class ShareRolePermissionsTest {
 @Test void unknownRoleCannotShareEvenWithWildcard() {
  UserPermissions permissions=new UserPermissions();
  for(String role:Set.of("unknown")) {
   ContaUsuario user=new ContaUsuario();user.setAtivo(true);user.setRole(role);user.setPermissao(Set.of("*"));
   assertFalse(permissions.has(user,"action_share"));assertFalse(permissions.has(user,"action_share_internal"));
  }
 }
 @Test void authorizedRolesKeepSharing() {
  UserPermissions permissions=new UserPermissions();
  for(String role:Set.of("publisher","helper","user","elder","superintendente","admin")) {
   ContaUsuario user=new ContaUsuario();user.setAtivo(true);user.setRole(role);
   assertTrue(permissions.has(user,"action_share"));assertTrue(permissions.has(user,"action_share_internal"));
  }
 }
 @Test void retakeIsAnIndependentPermission() {
  UserPermissions permissions=new UserPermissions(); ContaUsuario user=new ContaUsuario();user.setAtivo(true);user.setRole("helper");
  user.setPermissao(Set.of("action_view"));assertFalse(permissions.has(user,"action_retake"));
  user.setPermissao(Set.of("action_view","action_retake"));assertTrue(permissions.has(user,"action_retake"));
 }
 @Test void finishVisitsCanBeGrantedIndependently() {
  UserPermissions permissions=new UserPermissions();ContaUsuario user=new ContaUsuario();user.setAtivo(true);user.setRole("publisher");
  user.setPermissao(Set.of("action_view"));assertFalse(permissions.has(user,"action_finish_visit"));
  user.setPermissao(Set.of("action_view","action_finish_visit"));assertTrue(permissions.has(user,"action_finish_visit"));
 }
}
