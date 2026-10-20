package com.logirock.shipment_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Objeto de transferencia de datos para estandarizar las respuestas de error de la API.
 */
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {

    /**
     * Fecha y hora en la que se produjo el error.
     */
    @Schema(description = "Fecha y hora del error", example = "2026-10-20T10:15:30")
    private LocalDateTime timestamp;

    /**
     * Código de estado HTTP retornado.
     */
    @Schema(description = "Código HTTP", example = "409")
    private int status;

    /**
     * Nombre del error HTTP.
     */
    @Schema(description = "Nombre del estado HTTP", example = "Conflict")
    private String error;

    /**
     * Mensaje descriptivo del error.
     */
    @Schema(description = "Detalle legible del problema", example = "Transición de estado no permitida")
    private String message;

    /**
     * Ruta del endpoint que generó el error.
     */
    @Schema(description = "Ruta que produjo el error", example = "/api/envios/9b715c47-1a23-4b70-a146-fbf0ff47b001/estado")
    private String path;

    /**
     * Lista de errores de validación específicos de los campos.
     */
    @Schema(description = "Errores de validación por campo, cuando corresponda")
    private List<String> validationErrors;
}
