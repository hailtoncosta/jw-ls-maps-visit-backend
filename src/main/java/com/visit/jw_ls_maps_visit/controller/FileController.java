package com.visit.jw_ls_maps_visit.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController 
@RequestMapping("/api/files")
public class FileController {
    
    private final Path root;

    public FileController(@Value("${app.upload-dir:uploads}") String dir) {
        root = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel criar o diretorio de uploads", e);
        }
    }
    
    @PostMapping(consumes = "multipart/form-data")
    public Map<String, String> upload(@RequestPart("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("Arquivo vazio...");
        String ext = "";
        String n = file.getOriginalFilename();
        if (n != null && n.lastIndexOf('.') >=0) ext = n.substring(n.lastIndexOf('.'));
        String name = UUID.randomUUID() + ext;
        Files.copy(file.getInputStream(), root.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        return Map.of("file_url","/uploads/"+name);
    }
}
