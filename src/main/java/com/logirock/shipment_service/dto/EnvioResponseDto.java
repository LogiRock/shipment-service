package com.logirock.shipment_service.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import com.logirock.shipment_service.enums.EstadoEnvio;
import io.swagger.v3.oas.annotations.media.Schema;

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
    @Schema(description = "Estado actual", example = "PENDIENTE")
    private EstadoEnvio estado;
    @Schema(description = "UUID del repartidor asignado", example = "7f7a2b5b-51aa-4ce8-9c6e-345aaabf0d7b", nullable = true)
    private UUID repartidorId;

    /** Constructor compatible con respuestas que todavía no incluyen asignación. */
    public EnvioResponseDto(UUID id, String codigoSeguimiento, Double peso, Double ancho,
            Double alto, Double largo, EstadoEnvio estado) {
        this(id, codigoSeguimiento, peso, ancho, alto, largo, estado, null);
    }
}
