package com.logirock.shipment_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

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
    private LocalDateTime timestamp;

    /**
     * Código de estado HTTP retornado.
     */
    private int status;

    /**
     * Nombre del error HTTP.
     */
    private String error;

    /**
     * Mensaje descriptivo del error.
     */
    private String message;

    /**
     * Ruta del endpoint que generó el error.
     */
    private String path;

    /**
     * Lista de errores de validación específicos de los campos.
     */
    private List<String> validationErrors;
}