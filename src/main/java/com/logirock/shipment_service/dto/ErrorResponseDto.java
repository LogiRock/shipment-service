package com.logirock.shipment_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {

    @Schema(description = "Fecha y hora del error", example = "2026-10-20T10:15:30")
    private LocalDateTime timestamp;

    @Schema(description = "Código HTTP", example = "409")
    private int status;

    @Schema(description = "Nombre del estado HTTP", example = "Conflict")
    private String error;

    @Schema(description = "Detalle legible del problema", example = "Transición de estado no permitida")
    private String message;

    @Schema(description = "Ruta que produjo el error", example = "/api/v1/envios/9b715c47-1a23-4b70-a146-fbf0ff47b001/estado")
    private String path;

    @Schema(description = "Errores de validación por campo, cuando corresponda")
    private List<String> validationErrors;
}
