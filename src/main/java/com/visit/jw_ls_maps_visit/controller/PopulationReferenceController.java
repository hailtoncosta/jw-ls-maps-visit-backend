package com.visit.jw_ls_maps_visit.controller;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.visit.jw_ls_maps_visit.model.PopulationReference;
import com.visit.jw_ls_maps_visit.repository.PopulationReferenceRepository;
@RestController @RequestMapping("/api/population-references")
public class PopulationReferenceController {
 private final PopulationReferenceRepository repository;
 public PopulationReferenceController(PopulationReferenceRepository repository){this.repository=repository;}
 @GetMapping public List<PopulationReference> list(){return repository.findAll();}
}
