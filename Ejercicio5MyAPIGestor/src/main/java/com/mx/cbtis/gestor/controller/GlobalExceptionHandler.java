package com.mx.cbtis.gestor.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// F-01: convierte el rechazo de la base de datos (llave foránea, dato inválido)
// en una respuesta 409 Conflict controlada en lugar de un 500
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridad(DataIntegrityViolationException ex) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("status", 409);
        cuerpo.put("error", "Conflict");
        cuerpo.put("mensaje", "El registro hace referencia a datos que no existen o viola una restricción de la base de datos");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(cuerpo);
    }
}