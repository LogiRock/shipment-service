package com.logirock.shipment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import com.logirock.shipment_service.enums.EstadoEnvio;
import com.logirock.shipment_service.models.Direccion;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnvioResponseDto {

    private UUID id;
    private String codigoSeguimiento;
    private Double peso;
    private Double ancho;
    private Double alto;
    private Double largo;
    private EstadoEnvio estado;

    private UUID clienteId;
    private String clienteNombre;

    private Direccion origen;
    private Direccion destino;
}
