package com.logirock.shipment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDto {

    @Schema(description = "Identificador único del cliente", example = "2e0d43cc-4fa3-4dc8-9b0d-67f57bb3b001")
    private UUID id;
    @Schema(description = "Nombre completo", example = "Ana Pérez")
    private String nombre;
    @Schema(description = "Correo electrónico", example = "ana@example.com")
    private String email;
    @Schema(description = "Teléfono de contacto", example = "+5491100000000")
    private String telefono;
}
