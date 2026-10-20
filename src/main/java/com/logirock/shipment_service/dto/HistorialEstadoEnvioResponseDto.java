
package com.logirock.shipment_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logirock.shipment_service.enums.EstadoEnvio;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEstadoEnvioResponseDto {

    @Schema(description = "Identificador del registro", example = "0ab8b48c-fb5c-4177-9890-9c6a3439e855")
    private UUID idHistorial;
    @Schema(description = "Estado anterior", example = "PENDIENTE")
    private EstadoEnvio estadoAnterior;
    @Schema(description = "Estado nuevo", example = "EN_RUTA")
    private EstadoEnvio estadoNuevo;
    @Schema(description = "Fecha y hora del cambio", example = "20/10/2026 10:15")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime fechaCambio;
}
