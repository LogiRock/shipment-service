package com.logirock.shipment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDto {

    private UUID id;
    private String nombre;
    private String email;
    private String telefono;
}