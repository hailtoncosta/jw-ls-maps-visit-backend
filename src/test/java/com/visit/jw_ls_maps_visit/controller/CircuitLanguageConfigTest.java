package com.visit.jw_ls_maps_visit.controller;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import com.visit.jw_ls_maps_visit.model.CircuitLanguageConfig;
import com.visit.jw_ls_maps_visit.repository.CircuitLanguageConfigRepository;
import com.visit.jw_ls_maps_visit.security.CurrentUser;
class CircuitLanguageConfigTest {
 @Test void zeroIsSavedAndNegativeValuesAreRejected() {
  var repo=mock(CircuitLanguageConfigRepository.class);
  var controller=new CircuitLanguageConfigController(repo,mock(CurrentUser.class));
  var config=new CircuitLanguageConfig();config.setCircuitoId(UUID.randomUUID());config.setIdioma("Libras");
  config.setSpeakers(0d);config.setPopulationRef(0d);
  controller.create(config);verify(repo).save(config);
  config.setSpeakers(-1d);
  assertThrows(IllegalArgumentException.class,()->controller.create(config));
  config.setSpeakers(0d);config.setPopulationRef(-1d);
  assertThrows(IllegalArgumentException.class,()->controller.create(config));
  config.setPopulationRef(Double.NaN);
  assertThrows(IllegalArgumentException.class,()->controller.create(config));
  verify(repo,times(1)).save(any());
 }
}
