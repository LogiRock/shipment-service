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
import com.logirock.shipment_service.dto.ErrorResponseDto;
import com.logirock.shipment_service.services.RepartidorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST que gestiona las peticiones HTTP referentes a los repartidores.
 */
@RestController
@RequestMapping("/api/v1/repartidores")
@RequiredArgsConstructor
@Tag(name = "Repartidores", description = "Registro, consulta, actualización y eliminación de repartidores")
public class RepartidorController {

    /**
     * Servicio inyectado que contiene la lógica de negocio.
     */
    private final RepartidorService repartidorService;

    /**
     * Endpoint para dar de alta un nuevo repartidor.
     */
    @PostMapping
    @Operation(summary = "Registrar repartidor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Repartidor registrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<RepartidorResponseDto> registrarRepartidor(@Valid @RequestBody RepartidorRequestDto request) {
        RepartidorResponseDto response = repartidorService.registrarRepartidor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para consultar un repartidor por su identificador.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Consultar repartidor por UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repartidor encontrado"),
            @ApiResponse(responseCode = "404", description = "Repartidor inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<RepartidorResponseDto> obtenerRepartidor(
            @Parameter(description = "UUID del repartidor", required = true) @PathVariable UUID id) {
        RepartidorResponseDto response = repartidorService.obtenerRepartidor(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para consultar todos los repartidores registrados.
     */
    @GetMapping
    @Operation(summary = "Listar repartidores")
    @ApiResponse(responseCode = "200", description = "Listado de repartidores")
    public ResponseEntity<List<RepartidorResponseDto>> listarRepartidores() {
        List<RepartidorResponseDto> response = repartidorService.listarRepartidores();
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para actualizar la información de un repartidor existente.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Modificar repartidor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repartidor actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Repartidor inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<RepartidorResponseDto> modificarRepartidor(
            @Parameter(description = "UUID del repartidor", required = true) @PathVariable UUID id,
            @Valid @RequestBody RepartidorRequestDto request) {
        RepartidorResponseDto response = repartidorService.modificarRepartidor(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para eliminar físicamente un repartidor del sistema.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar repartidor")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Repartidor eliminado"),
            @ApiResponse(responseCode = "404", description = "Repartidor inexistente", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<Void> eliminarRepartidor(
            @Parameter(description = "UUID del repartidor", required = true) @PathVariable UUID id) {
        repartidorService.eliminarRepartidor(id);
        return ResponseEntity.noContent().build();
    }
}
