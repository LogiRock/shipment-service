package com.logirock.shipment_service.dto;

import com.logirock.shipment_service.enums.EstadoRepartidor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Objeto de transferencia de datos utilizado para la creación y actualización de un repartidor.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepartidorRequestDto {
    /**
     * Nombre completo del repartidor.
     */
    @Schema(description = "Nombre completo del repartidor", example = "Juan López", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /**
     * Tipo de vehículo asignado al repartidor.
     */
    @Schema(description = "Vehículo utilizado para los repartos", example = "Motocicleta", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El tipo de vehículo es obligatorio")
    private String tipoVehiculo;
    /**
     * Estado operativo actual del repartidor.
     */
    @Schema(description = "Estado operativo del repartidor", example = "ACTIVO", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El estado es obligatorio")
    private EstadoRepartidor estado;
}
