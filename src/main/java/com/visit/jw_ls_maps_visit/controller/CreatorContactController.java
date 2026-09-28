package com.visit.jw_ls_maps_visit.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visit.jw_ls_maps_visit.model.CreatorContact;
import com.visit.jw_ls_maps_visit.repository.CreatorContactRepository;

@RestController 
@RequestMapping("/api/creator-contact")
public class CreatorContactController {
    
    private final CreatorContactRepository creatorContactRepository;

    public CreatorContactController(CreatorContactRepository creatorRepo) {
        creatorContactRepository = creatorRepo;
    }

    @GetMapping("/listAll")
    public List<CreatorContact> list() {
        return creatorContactRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public CreatorContact getById(@PathVariable UUID id) {
        return creatorContactRepository.findById(id).orElseThrow();
    }
    
    @PostMapping("/save")
    public CreatorContact create(@RequestBody CreatorContact creator) {
        return creatorContactRepository.save(creator);
    }

    @PutMapping("/update/{id}")
    public CreatorContact update(@PathVariable UUID id, @RequestBody CreatorContact creator) {
        creator.setId(id);
        return creatorContactRepository.save(creator);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        creatorContactRepository.deleteById(id);
    }

}
