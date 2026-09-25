package com.visit.jw_ls_maps_visit.config;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice 
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<?> responseStatus(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("error",
            e.getReason() == null ? "Erro na solicitação" : e.getReason()));
    }
    
    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<?> notFound(Exception e) {
        return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    ResponseEntity<?> forbidden(Exception e) {
        return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    ResponseEntity<?> error(Exception e) {
        return ResponseEntity.status(400).body(Map.of("error", e.getMessage() == null ? "Erro" : e.getMessage()));
    }

}
