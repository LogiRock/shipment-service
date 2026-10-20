package com.logirock.shipment_service.dto;

import java.util.UUID;

import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.models.Direccion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Datos del envío que se exponen en las respuestas de la API. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnvioResponseDto {

    @Schema(description = "UUID del envío", example = "9b715c47-1a23-4b70-a146-fbf0ff47b001")
    private UUID id;

    @Schema(description = "Código de seguimiento", example = "ENV-A1B2C3D4")
    private String codigoSeguimiento;

    @Schema(description = "Peso en kilogramos", example = "2.5")
    private Double peso;

    @Schema(description = "Ancho en centímetros", example = "10.0")
    private Double ancho;

    @Schema(description = "Alto en centímetros", example = "20.0")
    private Double alto;

    @Schema(description = "Largo en centímetros", example = "30.0")
    private Double largo;

    @Schema(description = "Estado actual del envío", example = "PENDIENTE")
    private EstadoEnvio estado;

    @Schema(description = "UUID del repartidor asignado; null si todavía no hay asignación", example = "7f7a2b5b-51aa-4ce8-9c6e-345aaabf0d7b", nullable = true)
    private UUID repartidorId;

    @Schema(description = "UUID del cliente propietario del envío", example = "2e0d43cc-4fa3-4dc8-9b0d-67f57bb3b001")
    private UUID clienteId;

    @Schema(description = "Nombre del cliente propietario del envío", example = "Ana Pérez")
    private String clienteNombre;

    @Schema(description = "Dirección desde donde parte el envío")
    private Direccion origen;

    @Schema(description = "Dirección de destino del envío")
    private Direccion destino;
}
