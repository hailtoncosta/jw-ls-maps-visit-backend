package com.visit.jw_ls_maps_visit.controller;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.visit.jw_ls_maps_visit.model.Language;
import com.visit.jw_ls_maps_visit.repository.LanguageRepository;
class LanguageContractTest {
 @Test void frontendPayloadCanBeSavedAndReturnedUsingItsFieldNames() throws Exception {
  var mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
  var language = mapper.readValue("{\"name\":\"Malaysian Sign Language\",\"alternate_name\":\"\",\"code\":\"xml\",\"is_custom\":true,\"circuito_id\":null,\"created_by_name\":\"Admin\"}", Language.class);
  assertEquals("Malaysian Sign Language",language.getNome());
  assertEquals("xml",language.getCodigo());
  assertEquals("Admin",language.getCreatedByNome());
  var repo=mock(LanguageRepository.class);
  when(repo.save(language)).thenReturn(language);
  assertSame(language,new LanguageController(repo).create(language));
  var json=mapper.valueToTree(language);
  assertEquals("Malaysian Sign Language",json.get("name").asText());
  assertEquals("xml",json.get("code").asText());
 }
 @Test void legacyNamesRemainAcceptedAndMissingNameIsRejected() throws Exception {
  var mapper = new ObjectMapper();
  assertEquals("Libras",mapper.readValue("{\"nome\":\"Libras\",\"codigo\":\"bzs\"}", Language.class).getNome());
  var repo=mock(LanguageRepository.class);
  assertThrows(IllegalArgumentException.class,()->new LanguageController(repo).create(new Language()));
  verifyNoInteractions(repo);
 }
}
