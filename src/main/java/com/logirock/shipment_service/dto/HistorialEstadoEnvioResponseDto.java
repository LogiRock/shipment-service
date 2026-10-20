
package com.logirock.shipment_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logirock.shipment_service.enums.EstadoEnvio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEstadoEnvioResponseDto {

    private UUID idHistorial;
    private EstadoEnvio estadoAnterior;
    private EstadoEnvio estadoNuevo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime fechaCambio;
}
