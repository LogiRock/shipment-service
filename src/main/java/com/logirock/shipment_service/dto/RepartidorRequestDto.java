package com.logirock.shipment_service.dto;

import com.logirock.shipment_service.enums.EstadoRepartidor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /**
     * Tipo de vehículo asignado al repartidor.
     */
    @NotBlank(message = "El tipo de vehículo es obligatorio")
    private String tipoVehiculo;
    /**
     * Estado operativo actual del repartidor.
     */
    @NotNull(message = "El estado es obligatorio")
    private EstadoRepartidor estado;
}