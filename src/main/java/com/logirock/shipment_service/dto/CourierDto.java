package com.logirock.shipment_service.dto;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.logirock.shipment_service.enums.EstadoRepartidor;
import lombok.Getter;
import lombok.Setter;

/** Datos mínimos del courier necesarios para validar una asignación. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourierDto {
    private UUID id;
    private EstadoRepartidor estado;
}
