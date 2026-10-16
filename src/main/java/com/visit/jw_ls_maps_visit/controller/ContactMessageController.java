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

import com.visit.jw_ls_maps_visit.model.ContactMessage;
import com.visit.jw_ls_maps_visit.repository.ContactMessageRepository;

@RestController 
@RequestMapping("/api/contact-messages")
public class ContactMessageController {

    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageController(ContactMessageRepository contactRepo) {
        contactMessageRepository = contactRepo;
    }

    @GetMapping("/listAll")
    public List<ContactMessage> list() {
        return contactMessageRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public ContactMessage getById(@PathVariable UUID id) {
        return contactMessageRepository.findById(id).orElseThrow();
    }

    @PostMapping("/save")
    public ContactMessage create(@RequestBody ContactMessage contact) {
        return contactMessageRepository.save(contact);
    }

    @PutMapping("/update/{id}")
    public ContactMessage update(@PathVariable UUID id, @RequestBody ContactMessage contact) {
        contact.setId(id);
        return contactMessageRepository.save(contact);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id) {
        contactMessageRepository.deleteById(id);
    }
    
}
