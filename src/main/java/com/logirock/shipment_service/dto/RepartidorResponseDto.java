package com.logirock.shipment_service.dto;

import java.util.UUID;

import com.logirock.shipment_service.enums.EstadoRepartidor;

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
    private UUID id;

    /**
     * Nombre completo del repartidor.
     */
    private String nombre;

    /**
     * Tipo de vehículo asignado al repartidor.
     */
    private String tipoVehiculo;

    /**
     * Estado operativo actual del repartidor.
     */
    private EstadoRepartidor estado;
}