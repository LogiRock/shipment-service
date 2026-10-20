package com.logirock.shipment_service.dto;

import java.util.UUID;

import com.logirock.shipment_service.enums.EstadoRepartidor;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Objeto de transferencia de datos utilizado para exponer la información de un repartidor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepartidorResponseDto {

    /**
     * Identificador único del repartidor.
     */
    @Schema(description = "Identificador único del repartidor", example = "7f7a2b5b-51aa-4ce8-9c6e-345aaabf0d7b")
    private UUID id;

    /**
     * Nombre completo del repartidor.
     */
    @Schema(description = "Nombre completo", example = "Juan López")
    private String nombre;

    /**
     * Tipo de vehículo asignado al repartidor.
     */
    @Schema(description = "Vehículo utilizado", example = "Motocicleta")
    private String tipoVehiculo;

    /**
     * Estado operativo actual del repartidor.
     */
    @Schema(description = "Estado operativo", example = "ACTIVO")
    private EstadoRepartidor estado;
}
