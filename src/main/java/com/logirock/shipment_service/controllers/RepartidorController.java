package com.logirock.shipment_service.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logirock.shipment_service.dto.RepartidorRequestDto;
import com.logirock.shipment_service.dto.RepartidorResponseDto;
import com.logirock.shipment_service.services.RepartidorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST que gestiona las peticiones HTTP referentes a los repartidores.
 */
@RestController
@RequestMapping("/api/v1/repartidores")
@RequiredArgsConstructor
public class RepartidorController {

    /**
     * Servicio inyectado que contiene la lógica de negocio.
     */
    private final RepartidorService repartidorService;

    /**
     * Endpoint para dar de alta un nuevo repartidor.
     */
    @PostMapping
    public ResponseEntity<RepartidorResponseDto> registrarRepartidor(@Valid @RequestBody RepartidorRequestDto request) {
        RepartidorResponseDto response = repartidorService.registrarRepartidor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para consultar un repartidor por su identificador.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RepartidorResponseDto> obtenerRepartidor(@PathVariable UUID id) {
        RepartidorResponseDto response = repartidorService.obtenerRepartidor(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para consultar todos los repartidores registrados.
     */
    @GetMapping
    public ResponseEntity<List<RepartidorResponseDto>> listarRepartidores() {
        List<RepartidorResponseDto> response = repartidorService.listarRepartidores();
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para actualizar la información de un repartidor existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RepartidorResponseDto> modificarRepartidor(
            @PathVariable UUID id,
            @Valid @RequestBody RepartidorRequestDto request) {
        RepartidorResponseDto response = repartidorService.modificarRepartidor(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para eliminar físicamente un repartidor del sistema.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRepartidor(@PathVariable UUID id) {
        repartidorService.eliminarRepartidor(id);
        return ResponseEntity.noContent().build();
    }
}